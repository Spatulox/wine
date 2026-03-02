package com.spatulox.wine.domain.enum

enum class WineFormat(val displayName: String, val capacityMl: Int) {
    HALF_BOTTLE("Demi-bouteille", 375),
    BOTTLE("Bouteille standard", 750),
    MAGNUM("Magnum", 1500),
    JEROBOAM("Jéroboam", 3000),
    REHOBOAM("Réhoboam", 4500),
    METHUSELAH("Matusalem", 6000),
    SALMANAZAR("Salmanazar", 9000),
    BALTHAZAR("Balthazar", 12000),
    NEBUCHadneZZAR("Nabuchodonosor", 15000),
    HALF_JEROBOAM("Demi-Jéroboam", 1500),
    IMPERIAL("Impériale", 6000)
}