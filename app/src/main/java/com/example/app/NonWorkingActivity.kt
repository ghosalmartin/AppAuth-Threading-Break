package com.example.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import net.openid.appauth.*
import java.util.concurrent.atomic.AtomicReference

class NonWorkingActivity : AppCompatActivity() {

    private val RC_AUTH = 1234

    private val intentReference = AtomicReference<CustomTabsIntent>()
    private val authService by lazy {
        AuthorizationService(
            applicationContext,
            AppAuthConfiguration.Builder().build()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        sendCustomTabsIntentToExecutor()
        launchAuthIntentWhenCustomTabsIntentReady()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_AUTH) {
            if (data == null) {
                showToast("Data is null")
                return
            }
            val resp = AuthorizationResponse.fromIntent(data)
            val ex = AuthorizationException.fromIntent(data)

            resp?.authorizationCode?.let {
                showToast(it)
            }

            ex?.let {
                showToast(it.toString())
            }

        } else {
            showToast("Request code does not match")
        }
    }

    private fun showToast(textToShow: String) =
        Toast.makeText(this, textToShow, Toast.LENGTH_LONG).show().also {
            Log.d("NonWorkingActivity", textToShow)
        }

    private fun sendCustomTabsIntentToExecutor() {
        intentReference.set(
            authService.createCustomTabsIntentBuilder(AUTH_REQUEST.toUri()).apply {
                setToolbarColor(
                    ContextCompat.getColor(
                        this@NonWorkingActivity,
                        R.color.colorPrimary
                    )
                )
            }.build()
        )
    }

    private fun launchAuthIntentWhenCustomTabsIntentReady() {
        findViewById<Button>(R.id.trigger_auth_button).apply {
            setOnClickListener {

                startActivityForResult(
                    authService.getAuthorizationRequestIntent(
                        AUTH_REQUEST,
                        intentReference.get()
                    ), RC_AUTH
                )
            }
        }
    }
}