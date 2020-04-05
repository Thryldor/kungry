package ca.ulaval.ima.mp.api.model

data class Restaurant(
    val id : Int?,
    val cuisine	: ArrayList<Cuisine>?,
    val distance : String?,
    val review_count : Int?,
    val opening_hours : ArrayList<OpeningHour>?,
    val review_average : Float?,
    val location : Location?,
    val reviews : ArrayList<Review>?,
    val name : String?,
    val website : String?,
    val phone_number : String?,
    val image : String?,
    val type : String? // [ RESTO, BAR, SNACK ]
)
