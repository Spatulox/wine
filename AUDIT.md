# Audit du code : app Wine (Android / Compose / Room)

> Audit statique : tout le code source a été lu, mais l'app n'a été ni compilée ni lancée. Tout ce qui suit vient de la lecture du code. Les scénarios des points 🔴 ont été déroulés à la main.

## Sommaire

- [🔴 Bugs graves](#-bugs-graves-perte-de-données-crash-action-qui-échoue-sans-rien-dire)
- [🟠 Autres bugs et problèmes de logique](#-autres-bugs-et-problèmes-de-logique)
- [🎨 Problèmes d'affichage](#-problèmes-daffichage)
- [🛠 Améliorations](#-améliorations-architecture-performances-maintenance)
- [Priorités conseillées](#priorités-conseillées)

---

## 🔴 Bugs graves (perte de données, crash, action qui échoue sans rien dire)

### 1. On ne peut quasiment jamais supprimer une ligne (étagère) d'un compartiment, et l'échec est silencieux

- **Fichiers :** `CompartmentRepositoryImpl.kt:73`, `CompartmentActionDialog.kt:266` et `:302`, `CompartmentViewModel.kt:53`
- **Cause :** `CompartmentRepositoryImpl.kt:73` décale les `order` existants de `shelves.size` pour éviter la contrainte `UNIQUE(compartmentId, order)`. Mais après une suppression, `shelves.size` est plus petit que le nombre d'étagères en base.
- **Exemple :** avec 3 étagères (ordres 0, 1, 2), on en supprime une et `size = 2`. L'étagère d'ordre 0 passe à 2, ce qui entre en collision avec l'étagère d'ordre 2 existante. On obtient une `SQLiteConstraintException`, puis un rollback.
- **Pourquoi personne ne le voit :** le ViewModel renvoie `false`, mais l'écran l'ignore et ferme quand même (`CompartmentActionDialog.kt:302`).
- **Correctif :**
  - décaler de `maxOrder + 1` (ou utiliser des ordres négatifs temporaires) ;
  - supprimer les étagères retirées avant de mettre à jour les autres ;
  - renuméroter la liste côté UI après une suppression (`CompartmentActionDialog.kt:266`) ;
  - afficher une erreur quand `update` renvoie `false`.

### 2. Modifier un compartiment le fait passer en dernière position

- **Fichiers :** `CompartmentActionDialog.kt:112` et `:297`
- **Cause :** à la mise à jour, `order = compOrder`, qui vaut « dernier ordre + 1 ».
- **Correctif :** garder l'ordre existant du compartiment lors d'une modification.

### 3. Crash si on renomme un vin en doublon

- **Fichiers :** `WineViewModel.kt:100`, `WineScreen.kt:60`
- **Scénario :** on donne à un vin le même nom, la même année et le même format qu'un vin existant.
- **Cause :** `updateWine` ne rattrape pas la `SQLiteConstraintException`, et l'exception remonte dans `coroutineScope.launch`, ce qui fait planter l'app.
- **Correctif :** `try/catch` comme dans `addWine`, retour d'un booléen et message à l'utilisateur.

### 4. Toute la cave peut être effacée à la prochaine mise à jour du schéma

- **Fichiers :** `DatabaseProvider.kt:17`, `AppDatabase.kt:16`
- **Cause :** `fallbackToDestructiveMigration(true)` avec une base en version 26 : n'importe quel changement de schéma sans `Migration` supprime les données de l'utilisateur.
- **Correctif :** écrire des `Migration`, et configurer `room.schemaLocation` pour exporter le schéma.

### 5. Une bouteille peut être déplacée sans que l'utilisateur l'ait voulu

- **Fichiers :** `dragGesture.kt:23`, `CompartmentScreen.kt:86-91` et `:329`
- **Cause :** `dragGesture` ne teste jamais `isEnabled`, donc le glisser après appui long marche aussi hors du mode édition.
- **Scénario :**
  1. Hors mode édition, on fait un appui long sur une bouteille, on glisse vers un emplacement vide et on relâche.
  2. Il ne se passe rien sur le moment, mais `draggedPosition`, `hoveredPosition` et `endOfDrag` restent en mémoire.
  3. Dès qu'on active ensuite le mode édition, la condition de `CompartmentScreen.kt:329` devient vraie et la bouteille est déplacée.
- **Correctif :** `if (!isEnabled) return@pointerInput` dans `dragGesture`, et remettre l'état de glisser à zéro à chaque changement de `isEditing`.

### 6. Le glisser-déposer peut viser le mauvais emplacement

- **Fichiers :** `BottleGrid.kt:89` et `:169`, `CompartmentScreen.kt:83`
- **Cause :** `rectBounds` (une map `Rect → Position`) reçoit une nouvelle entrée à chaque placement : à chaque frame de défilement, à chaque changement de liste. Les anciennes entrées ne sont jamais retirées.
- **Conséquences :**
  - après un défilement, `findTargetPosition` peut tomber sur l'ancien emplacement d'une bouteille ;
  - la map grossit sans limite, et chaque événement de glisser la parcourt entièrement.
- **Correctif :** supprimer `rectBounds` et faire le test de position uniquement sur `positionBounds` (`Position → Rect`).

### 7. Le bandeau « Moving » reste affiché si on relâche la bouteille dans le vide

- **Fichiers :** `CompartmentScreen.kt:242` et `:290`
- **Cause :** `onDragEnd` met seulement `endOfDrag = true`. Si aucune cible n'est survolée, `draggedPosition` n'est jamais remis à `null`.
- **Conséquence :** le bandeau et la bouteille semi-transparente restent affichés.
- **Correctif :** dans `onDragEnd`, remettre l'état à zéro quand `hoveredPosition == null`.

### 8. La couleur choisie dans « Nouveau vin » est écrasée

- **Fichier :** `WineAddDialog.kt:99`
- **Cause :** `wineColor = MaterialTheme.colorScheme.primary` est exécuté pendant la composition, donc à chaque recomposition la couleur choisie est remplacée.
- **Autre problème :** c'est la couleur dynamique du thème (fond d'écran ou mode sombre) qui est enregistrée en base.
- **Correctif :** initialiser l'état une seule fois dans le `remember`, avec une couleur fixe.

### 9. Le champ prix est inutilisable

- **Fichiers :** `WineAddDialog.kt:72` et `:238`
- **Cause :** le texte affiché (`priceText`) vient de `unitPrice.toString()`. Conséquences :
  - quand on tape « 1 », le champ affiche « 1.0 » ;
  - quand on tape ensuite « 2 », on obtient « 1.02 » ;
  - on ne peut plus vider le champ (il repasse à `0f` au lieu de `null`) ;
  - la virgule est filtrée, alors que c'est le séparateur décimal du clavier français.
- **Il manque aussi :** un champ prix dans `WineEditDialog`, donc impossible de corriger un prix.
- **Correctif :** garder un état `String` pour le texte saisi, accepter `,` et `.`, et ne convertir qu'à la validation.

### 10. Le filtre par année reste bloqué

- **Fichiers :** `WineDao.kt:39`, `DateSelection.kt:42` et `:65`
- **Cause :** `SELECT year FROM wine` n'a pas de `DISTINCT`. Avec deux vins de 2018, `indexOf(2018)` renvoie toujours le même index, et on ne peut jamais atteindre 2020.
- **Correctif :** `SELECT DISTINCT year FROM wine ORDER BY year`.

### 11. Les filtres rouverts ne trouvent plus rien

- **Fichier :** `SearchWithFilter.kt:215`, `:241`, `:264-267`
- **Cause :**
  - quand on ré-ouvre la recherche, le filtre est réappliqué avec `format.name` ou `type.name` (par exemple `"BOTTLE"`) au lieu de `displayName`, donc le résultat est vide ;
  - le cas `"region"` utilise `isFormatInit` au lieu de son propre drapeau (copier-coller).

### 12. La cave devient multicolore si un filtre ne donne aucun résultat

- **Fichier :** `BottleGrid.kt:139`
- **Cause :** le cas `wines.isEmpty()` affiche la palette arc-en-ciel. La même chose se produit brièvement au démarrage, tant que la `StateFlow` a sa valeur initiale vide.

### 13. On peut ranger un vin qui n'a plus de bouteilles

- **Fichiers :** `OnBottlePositionClick.kt:82-87`, `WineDao.kt:48`
- **Cause :**
  - `excludeWineIds` ne regarde que les vins déjà présents dans le stock : un vin avec `qte = 0` et aucune bouteille rangée reste proposé ;
  - ensuite, `qte = qte - 1` peut devenir négatif.
- **Correctif :** calculer « bouteilles restantes = `qte` − bouteilles rangées » pour tous les vins, et ajouter `WHERE qte > 0` au retrait.

### 14. Des transactions sont validées à moitié

- **Fichiers :** `CompartmentRepositoryImpl.kt:51` et `:79`, `CompartmentViewModel.kt:46` et `:55`
- **Cause :** dans `update()` et `updateOrder()`, `return@run -1` ou `false` à l'intérieur de `withTransaction` valide tout ce qui a déjà été fait. Il faut lever une exception pour annuler.
- **Et en plus :** le ViewModel renvoie `true` quelle que soit la valeur de retour du repository.

### 15. Deux opérations qui devraient être atomiques ne le sont pas

- **Déplacement** (`CompartmentScreen.kt:348-349`) : un `delete` puis un `insert`. Si l'insert échoue, la bouteille est perdue. Un simple `update(it.copy(position = to))` suffit.
- **Retrait** (`CompartmentScreen.kt:376-379`) : le stock puis `qte` sont modifiés en deux appels séparés. Il faut une seule transaction dans le repository.

---

## 🟠 Autres bugs et problèmes de logique

- **Rotation de l'écran ou fermeture du process par Android :**
  - Les ViewModels sont créés à la main dans `onCreate` (`MainActivity.kt:43-46`). Ils sont recréés à chaque rotation, `onCleared` n'est jamais appelé, et le mode édition est perdu.
  - Tous les états utilisent `remember` au lieu de `rememberSaveable`.
  - L'écran d'édition de compartiment se retrouve vide : `getCompartmentById` lit `compartments.value`, qui est encore `emptyList()` (`CompartmentActionDialog.kt:127-141`).
- **La route d'édition déclare deux fois le même argument :** `COMPARTMENT_EDIT` contient déjà `{compartmentId}` et on y ajoute `/{compartmentId}` (`Destinations.kt:8`, `AppNavGraph.kt:48`, `CompartmentScreen.kt:255`). L'URL obtenue est `compartment/edit/{compartmentId}/5`. Ça marche par chance.
- **Édition de la quantité d'un vin :**
  - le message « impossible de mettre moins… » s'affiche quand `qte == stock`, ce qui est une valeur valide : `<=` au lieu de `<` (`WineEditDialog.kt:217`) ;
  - le bouton « Valider » n'est jamais désactivé.
- **`NumberField` limite la valeur à chaque frappe (`NumberField.kt:95`).** Avec un minimum de 5, si on efface le champ pour taper 12, il affiche « 5 », puis « 51 » quand on tape « 1 ». Il faudrait appliquer le minimum seulement à la perte de focus ou à la validation.
- **Contrôle de doublon à l'ajout :** il inclut `type` et ignore la casse, alors que l'index unique porte sur `(name, year, format)` (`WineAddDialog.kt:78-88`). De plus, la boîte de dialogue se ferme avant le résultat de l'insertion (`WineScreen.kt:161-167`), donc la saisie est perdue en cas d'échec.
- **Suppressions sans confirmation :**
  - compartiment (`CompartmentActionDialog.kt:176`) ;
  - vin (`WineEditDialog.kt:120`) ;
  - le bouton avec l'icône « Déplacer » supprime en réalité le stock (`OnBottlePositionClick.kt:122-126`).
- **Effets de bord exécutés pendant la composition :**
  - `onYearChange(displayYear)` à chaque recomposition (`DateSelection.kt:32`) ;
  - `onChangeTabScreen(...)` (`WineScreen.kt:50`) ;
  - `onMove` lancé directement depuis `MoveBottleDialog` (`MoveBottleDialog.kt:18`) ;
  - écriture d'état puis `return` dans un composable (`CompartmentScreen.kt:331-334`) ;
  - `wineColor = ...` (`WineAddDialog.kt:99`).

  Tout ça devrait être dans des callbacks ou des `LaunchedEffect`.
- **L'utilisateur voit des textes d'exception bruts :** `e.toString()` est affiché dans le snackbar (`CompartmentViewModel.kt:66`).
- **La couleur choisie peut être perdue :** `ButtonColorPicker` garde la couleur en cours dans une variable locale (`var displayColor`, `ButtonColorPicker.kt:47`) au lieu d'un `remember { mutableStateOf }`. Une recomposition pendant le choix fait perdre la sélection.

---

## 🎨 Problèmes d'affichage

### A. Texte coupé avec un caractère par ligne dans la liste des vins

- **Fichier :** `WineItem.kt:95-128`
- **Cause :** cette `Row` contient cinq `Text` sans `weight` ni `maxLines` : année, type, région, format et prix.
- **Exemple :** « 2020 », « Rouge », « Languedoc-Roussillon », « Bouteille standard », « 12.5 € ». Ça fait environ 450 dp de contenu pour environ 300 dp disponibles. Les derniers textes reçoivent une largeur presque nulle et se replient caractère par caractère.
- **Correctif :** utiliser `FlowRow`, ou répartir sur deux lignes avec `maxLines = 1` et `TextOverflow.Ellipsis`.

### B. Même problème dans la fenêtre d'une bouteille

- **Fichier :** `OnBottlePositionClick.kt:146-174`
- Le type a `weight(1f)` mais il est mesuré après l'année et la région. Avec « Beaujolais et Lyonnais », « Champagne » se replie.
- `Text(wine.comment)` (`:172`) n'a pas de limite de hauteur et la colonne ne défile pas : un long commentaire pousse les boutons « Retirer » et « Annuler » hors de l'écran.
- Un commentaire vide affiche quand même une ligne vide, avec son espacement.

### C. Le bandeau « X bouteille(s) à ranger » peut masquer toute la cave

- **Fichier :** `CompartmentScreen.kt:160-177`
- Il n'a pas de hauteur maximale et se trouve hors de la `LazyColumn`. Avec 15 vins à ranger, il occupe tout l'écran et la cave ne se voit plus.
- **Correctif :** en faire un `item` de la liste, ou le rendre repliable.

### D. En-tête de la fenêtre de modification d'un vin

- **Fichier :** `WineEditDialog.kt:103-128`
- Le titre n'a pas de `weight`. Avec une grande taille de police système, le bouton supprimer est écrasé.
- Il n'y a pas d'espace entre la pastille de couleur et le titre.

### E. Le FAB de recherche déplié recouvre l'autre FAB

- **Fichiers :** `SearchWithFilter.kt:155-158`, `MainMenu.kt:73-109`
- Le FAB de recherche déplié prend toute la largeur (`fillMaxWidth`) et recouvre le FAB Éditer/Ajouter en bas à gauche.

### F. Bouteilles en quinconce coupées

- **Fichiers :** `BottleGrid.kt:118-122` et `:232`
- Les décalages en quinconce utilisent `offset(x = ±26dp)`, qui ne réserve pas d'espace dans la mise en page. La première ou la dernière bouteille est coupée par le bord de la `LazyRow` quand la rangée défile.

### G. Emplacements vides et bouteilles filtrées impossibles à distinguer

- **Fichier :** `BottleGrid.kt:157`
- Un emplacement vide et une bouteille masquée par le filtre ont la même couleur (`onSurfaceVariant`). Il faudrait par exemple un simple contour pour les emplacements vides.

### H. Petits défauts

- L'état vide de `WineScreen.kt:122` utilise `fillMaxSize` dans une liste lazy, donc il n'est pas centré (il faut `fillParentMaxSize`).
- L'icône blanche de `ButtonColorPicker.kt:63` est invisible sur les couleurs claires, et la barre alpha permet de choisir une couleur transparente.
- « null » s'affiche une frame dans `DateSelection.kt:55`.
- Les champs de liste déroulante (`EnumDropdownField`, `WineDropdownList`) ne sont pas `singleLine`, donc ils s'agrandissent dans le FAB.
- Les prix s'affichent « N/A € » ou « 12.5 € » au lieu de « 12,50 € » (`WineItem.kt:124`).

---

## 🛠 Améliorations (architecture, performances, maintenance)

### Architecture

- **ViewModels :** passer par `by viewModels { factory }` (ou Hilt), avec `SavedStateHandle`.
- **Couches mélangées :**
  - `CompartmentRepositoryImpl` importe `ui.CompartmentScreen` ;
  - l'interface `ShelfRepository` importe Room et des entités ;
  - le modèle `Wine` utilise la `Color` de Compose ;
  - les ViewModels dépendent des classes `*Impl` plutôt que des interfaces ;
  - `FilterViewModel` dépend d'un `Filter` défini dans l'UI.
- **Filtres :** le champ `Filter.field` est une chaîne. Une `sealed class` serait plus sûre.
- **Argent en `Float` :** stocker des centimes en `Long`, et formater avec `NumberFormat.getCurrencyInstance(Locale.FRANCE)`.
- **Modèle mutable :** `Wine.stars` est un `var` dans une `data class`. Il devrait être `val`.

### Performances

- **Flux dupliqués :** `getStockStream()` est collecté 4 fois, ce qui fait 4 requêtes `@Relation` à chaque changement. `countWineIdStocked` et `stockDistinctWineCount` sont identiques. Il vaut mieux un seul flux partagé avec `shareIn`. Même chose pour `getWineStream()`.
- **Vérifier l'existence de stock :** utiliser `SELECT EXISTS(...)` au lieu de charger un `StockWithWineEntity` (`StockDao.kt:31-37`).
- **Calcul coûteux :** `unrackedWines` compte les bouteilles de chaque vin en parcourant tout le stock (`CompartmentScreen.kt:93-103`). Un `groupingBy` suffit.
- **Listes sans `key` :** `items(compartment.size)` dans `CompartmentScreen.kt:185`.
- **Palette recréée :** la liste de couleurs est reconstruite pour chaque bouteille à chaque recomposition (`BottleGrid.kt:140`).

### Code mort

- `IconPicker.kt` (309 lignes, importé mais pas utilisé), `CommentCard`, `MoveBottleDialog`.
- `Destinations.SHELF` et `Destinations.WINE`, le modèle `Stock`.
- Les méthodes DAO `getStockWithWine`, `getAllShelves`, `getStockByShelf`, `search`, `getCount`, `getByYear`, et l'état `winesByYearAsc`.
- Les paramètres `reason`, le champ `Shelf.name` (toujours vide).
- Beaucoup d'imports inutiles, dont trois `Icon` différents dans `CompartmentActionDialog`.

### Build et projet

- `androidx.xr.compose:compose-testing` (une bibliothèque alpha pour la XR) est déclaré en `implementation`, donc embarqué dans l'APK (`app/build.gradle.kts:58`).
- Les versions sont mélangées : BOM 2024.09, mais material3 et animation fixés ailleurs ; core, lifecycle et activity sont anciens.
- R8 est désactivé (`isMinifyEnabled = false`) alors que `material-icons-extended` est lourd.
- `room.schemaLocation` n'est pas configuré.
- Le `.gitignore` exclut `test` et `androidTest`, donc aucun test ne peut être commité.

### Qualité et finitions

- **Textes :** tout est codé en dur et mélange français et anglais (« Duplicate Entry », « Moving », « Wine exist in cave… », « There is stock inside… »). Il faudrait passer par `strings.xml`.
- **Coquilles dans les libellés affichés :**
  - « Mousseaux » → « Mousseux » ;
  - « Matusalem » → « Mathusalem » ;
  - « Nombres de bouteilles » → « Nombre de bouteilles ».
- **Coquilles dans les noms du code :** `aligment`, `NEBUCHadneZZAR`, `distincWineCounts`, `getwineYearsStream`. `aligment` et les noms d'enum sont stockés en base, donc les renommer demande une migration.
- **Icônes dépréciées :** `Icons.Default.ArrowBack`, `KeyboardArrowLeft/Right` et `CompareArrows` ne s'inversent pas en écriture de droite à gauche (utiliser les versions `AutoMirrored`). `Modifier.menuAnchor()` sans paramètre est déprécié.
- **Accessibilité :** les emplacements de bouteilles n'ont ni sémantique ni description.

---

## Priorités conseillées

Les points à traiter en premier, parce qu'ils sont les plus visibles pour l'utilisateur et se corrigent localement :

1. **#1** : suppression d'étagère qui échoue sans rien dire
2. **#2** : compartiment modifié qui passe en dernière position
3. **#3** : crash au renommage d'un vin en doublon
4. **#5** : déplacement involontaire de bouteille
5. **#7** : bandeau « Moving » qui reste affiché
6. **A** : texte coupé caractère par caractère dans la liste des vins

Ensuite, avant toute évolution du schéma de base : **#4** (migrations Room), pour ne pas effacer la cave des utilisateurs.
