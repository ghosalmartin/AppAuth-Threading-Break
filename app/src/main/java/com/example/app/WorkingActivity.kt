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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

class WorkingActivity : AppCompatActivity() {

    private val RC_AUTH = 123

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val intentReference = AtomicReference<CustomTabsIntent>()
    private val authService by lazy {
        AuthorizationService(
            applicationContext,
            AppAuthConfiguration.Builder().build()
        )
    }

    private val mAuthIntentLatch = CountDownLatch(1)

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
            Log.d("WorkingActivity", textToShow)
        }

    private fun sendCustomTabsIntentToExecutor() {
        executor.submit {
            intentReference.set(
                authService.createCustomTabsIntentBuilder(AUTH_REQUEST.toUri()).apply {
                    setToolbarColor(
                        ContextCompat.getColor(
                            this@WorkingActivity,
                            R.color.colorPrimary
                        )
                    )
                }.build()
            )
            mAuthIntentLatch.countDown()
        }
    }

    private fun launchAuthIntentWhenCustomTabsIntentReady() {
        findViewById<Button>(R.id.trigger_auth_button).apply {
            setOnClickListener {
                mAuthIntentLatch.await()

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