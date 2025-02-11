package ca.ulaval.ima.mp.api.model

import ca.ulaval.ima.mp.api.Config

data class AccountLogin(
    val client_id : String? = Config.clientID,
    val client_secret : String? = Config.clientSecret,
    val email : String?,
    val password : String?
)
