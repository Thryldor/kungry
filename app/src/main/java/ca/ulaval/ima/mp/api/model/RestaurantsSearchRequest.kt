package ca.ulaval.ima.mp.api.model

data class RestaurantsSearchRequest(
    val page: Int?,
    val page_size: Int?,
    val latitude: Int?,
    val longitude: Int?,
    val radius: Int?,
    val text: String?
)
