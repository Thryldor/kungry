package ca.ulaval.ima.mp.api.model

data class RestaurantLight(
    val id : Int?,
    val name : String?,
    val cuisine : ArrayList<Cuisine>?,
    val type : String?, // [ RESTO, BAR, SNACK ]
    val review_count : Int?,
    val review_average : Float?,
    val image : String?,
    val distance : String?,
    val location : Location?
)
