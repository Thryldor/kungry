package ca.ulaval.ima.mp.api.model

data class RestaurantGetReviewsRequest(
    val id: Int?,
    val page: Int?,
    val page_size: Int?
)
