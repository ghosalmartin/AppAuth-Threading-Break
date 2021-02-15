package com.example.app

import android.net.Uri
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

const val REDIRECT_URI = "https://dev-8983037.okta.com/test"

const val AUTHORIZE_URI = "https://dev-8983037.okta.com/oauth2/default/v1/authorize"
const val TOKEN_URI = "https://dev-8983037.okta.com/oauth2/default/v1/token"

const val CLIENT_ID = "0oa3e87hv8jXgffWK5d6"

const val SCOPES = "openid profile"

val AUTH_REQUEST by lazy {
    val serviceConfig = AuthorizationServiceConfiguration(
        Uri.parse(AUTHORIZE_URI),
        Uri.parse(TOKEN_URI)
    )

    AuthorizationRequest.Builder(
        serviceConfig,
        CLIENT_ID,
        ResponseTypeValues.CODE,
        Uri.parse(REDIRECT_URI)
    )
        .setScope(SCOPES)
        .build()
}