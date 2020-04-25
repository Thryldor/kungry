package ca.ulaval.ima.mp.tools

class TypeConverter {
    companion object {
        fun convert(type: String): String {
            return when (type) {
                "RESTO" -> "Restaurant | Repas entre amis"
                "BAR" -> "Pub/bar - Repas Gastronomique"
                "SNACK" -> "Snack/Food • Confort food"
                else -> ""
            }
        }
    }
}