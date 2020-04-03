package ca.ulaval.ima.mp.api.model

data class RefreshTokenInput(
    val refresh_token : String?,
    val client_id : String?,
    val client_secret : String?
)
