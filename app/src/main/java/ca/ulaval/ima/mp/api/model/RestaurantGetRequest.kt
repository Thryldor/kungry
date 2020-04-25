package ca.ulaval.ima.mp.api.model

data class RestaurantGetRequest(
    val latitude: Double?,
    val longitude: Double?,
    val id: Int?
)
