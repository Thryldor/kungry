package ca.ulaval.ima.mp.api.model

import ca.ulaval.ima.mp.api.Config

data class RefreshTokenInput(
    val refresh_token : String?,
    val client_id : String? = Config.clientID,
    val client_secret : String? = Config.clientSecret
)
