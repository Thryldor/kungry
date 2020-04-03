package ca.ulaval.ima.mp.api.model

import ca.ulaval.ima.mp.api.Config

data class CreateAccount(
    val client_id : String? = Config.clientID,
    val client_secret : String? = Config.clientSecret,
    val first_name : String?,
    val last_name : String?,
    val email : String?,
    val password : String?
)
