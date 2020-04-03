package ca.ulaval.ima.mp.api.model

data class TokenOutput(
    val access_token : String?,
    val token_type : String?,
    val refresh_token : String?,
    val scope : String?,
    val expires_in : Int?
)
