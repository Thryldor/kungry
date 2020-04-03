package ca.ulaval.ima.mp.api.model

data class OpeningHour(
    val id : Int?,
    val opening_hour : String?,
    val day : String? // [ SUN, MON, TUE, WED, THU, FRI, SAT ]
)
