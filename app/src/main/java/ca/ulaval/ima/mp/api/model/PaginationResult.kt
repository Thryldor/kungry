package ca.ulaval.ima.mp.api.model;

data class PaginationResult<T>(
    val count: Int?,
    val next: Int?,
    val previous: Int?,
    val results: ArrayList<T>
)
