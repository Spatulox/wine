package com.spatulox.wine.domain.enum

// Since wine are french, use the french words
enum class WineType(val displayName: String) {
    ROUGE("Rouge"),
    BLANC("Blanc"),
    ROSE("Rosé"),
    CHAMPAGNE("Champagne"),
    MOUSSEUX("Mousseaux"),
    AUTRE("Autre")
}