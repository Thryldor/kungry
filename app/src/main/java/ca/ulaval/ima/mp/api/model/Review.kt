package ca.ulaval.ima.mp.api.model

data class Review(
    val id : Int?,
    val creator : Creator?,
    val stars : Int?,
    val image : String?,
    val comment : String?,
    val date : String?
)
