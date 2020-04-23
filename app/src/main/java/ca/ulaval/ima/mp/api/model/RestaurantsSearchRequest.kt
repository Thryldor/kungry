package ca.ulaval.ima.mp.api.model

data class RestaurantsSearchRequest(
    val page: Int?,
    val page_size: Int?,
    val latitude: Double?,
    val longitude: Double?,
    val radius: Int?,
    val text: String?
)
