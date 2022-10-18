package com.nin0dev.qweb

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.Executor

class PinActivity : AppCompatActivity() {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)
        handleBioUnlock()
        findViewById<ExtendedFloatingActionButton>(R.id.okButton).setOnClickListener {
            val sp = getSharedPreferences("privacy", Context.MODE_PRIVATE)
            if(sp.getString("pinCode", "") == findViewById<EditText>(R.id.pin_code).text.toString())
            {
                finish()
                startActivity(Intent(this, MainActivity::class.java).putExtra("authorized", true))
            }
            else
            {
                findViewById<EditText>(R.id.pin_code).setText("")
                Toast.makeText(this, "Invalid PIN", Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<ExtendedFloatingActionButton>(R.id.fpUnlockButton).setOnClickListener {
            requestBioUnlock()
        }
    }

    fun handleBioUnlock()
    {
        val bm = BiometricManager.from(this)
        val sp = getSharedPreferences("privacy", Context.MODE_PRIVATE)
        if(bm.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS || !sp.getBoolean("biometricUnlock", false))
        {
            findViewById<ExtendedFloatingActionButton>(R.id.fpUnlockButton).visibility = GONE
            findViewById<TextView>(R.id.textView3).text = "Enter your QWeb PIN to continue"
        }
    }

    fun requestBioUnlock()
    {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if(errString != "Cancel" && errorCode != 10)
                    {
                        Toast.makeText(applicationContext,
                            "Error: $errString", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    finish()
                    startActivity(Intent(applicationContext, MainActivity::class.java).putExtra("authorized", true))
                    super.onAuthenticationSucceeded(result)
                }

                override fun onAuthenticationFailed() {

                    super.onAuthenticationFailed()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Confirm identity")
            .setNegativeButtonText("Cancel")
            .build()
        biometricPrompt.authenticate(promptInfo)
    }
}