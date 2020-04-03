package ca.ulaval.ima.mp.api.model

data class AccountLogin(
    val client_id : String?,
    val client_secret : String?,
    val email : String?,
    val password : String?
)
