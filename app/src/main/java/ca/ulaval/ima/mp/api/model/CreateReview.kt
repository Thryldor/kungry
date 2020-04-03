package ca.ulaval.ima.mp.api.model

data class CreateReview(
    val restaurant_id : Int?,
    val stars : Int?,
    val comment : String?
)
