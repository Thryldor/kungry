package ca.ulaval.ima.mp.api.model
data class CreateAccountCreate(
    val client_id : String?,
    val client_secret : String?,
    val first_name : String?,
    val last_name : String?,
    val email : String?,
    val password : String?
)
