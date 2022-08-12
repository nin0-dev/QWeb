package com.nin0dev.qweb

import android.content.Context
import android.content.Intent
import android.os.Build
    import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.text.InputType
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Switch
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.Executor

class SettingsActivity : AppCompatActivity() {
    private lateinit var executor: Executor
    private var savetfBek = 0
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        readSettings()
        listeners()
        checkBiometrics()
    }

    fun checkBiometrics()
    {
        val bm = BiometricManager.from(this)
        if(bm.canAuthenticate(BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS)
        {
            findViewById<Switch>(R.id.fp_unlock).isChecked = false
        }
    }

    fun listeners()
    {
        findViewById<MaterialToolbar>(R.id.materialToolbar4).setNavigationOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.accSettings).setOnClickListener {
            startActivity(Intent(applicationContext, SeparateActivity::class.java).putExtra("url", "https://www.quora.com/settings"))
        }
        findViewById<Button>(R.id.saveSettings).setOnClickListener {
            writeSettings()
        }
        findViewById<Switch>(R.id.fp_unlock).setOnClickListener {
            if(findViewById<EditText>(R.id.pin_code).text.toString() == "")
            {
                Toast.makeText(this, "Biometric unlock can't be enabled if a PIN code is not set", Toast.LENGTH_LONG).show()
                findViewById<Switch>(R.id.fp_unlock).isChecked = false
                return@setOnClickListener
            }
            executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int,
                                                       errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        val oldState = !findViewById<Switch>(R.id.fp_unlock).isChecked
                        if(errString != "Cancel" && errorCode != 10)
                        {
                            Toast.makeText(applicationContext,
                                "Error: $errString", Toast.LENGTH_SHORT)
                                .show()
                        }
                        findViewById<Switch>(R.id.fp_unlock).isChecked = oldState
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                    }

                    override fun onAuthenticationFailed() {
                        findViewById<Switch>(R.id.fp_unlock).isChecked = false
                        super.onAuthenticationFailed()
                    }
                })

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Confirm identity")
                .setNegativeButtonText("Cancel")
                .build()
            biometricPrompt.authenticate(promptInfo)
        }

        findViewById<Button>(R.id.show_pin).setOnClickListener {
            findViewById<EditText>(R.id.pin_code).inputType = InputType.TYPE_CLASS_NUMBER
            findViewById<Button>(R.id.show_pin).visibility = GONE
            findViewById<Button>(R.id.hide_pin).visibility = VISIBLE
        }
        findViewById<Button>(R.id.hide_pin).setOnClickListener {
            findViewById<EditText>(R.id.pin_code).inputType = InputType.TYPE_NUMBER_VARIATION_PASSWORD + InputType.TYPE_CLASS_NUMBER
            findViewById<Button>(R.id.show_pin).visibility = VISIBLE
            findViewById<Button>(R.id.hide_pin).visibility = GONE
        }
    }

    fun readSettings()
    {

        val spTweaks = getSharedPreferences("tweaks", Context.MODE_PRIVATE)
        val spTheme = getSharedPreferences("theme", Context.MODE_PRIVATE)
        val spPrivacy = getSharedPreferences("privacy", Context.MODE_PRIVATE)

        if(spTweaks.getBoolean("adBlock", true))
        {
            findViewById<CheckBox>(R.id.block_ads).isChecked = true
        }
        if(spTweaks.getBoolean("oldProfile", true))
        {
            findViewById<CheckBox>(R.id.old_profile_layout).isChecked = true
        }

        if(Build.VERSION.SDK_INT >= 29)
        {
            // Dark mode supported
            when (spTheme.getInt("theme", 3)) {
                1 ->  {
                    findViewById<RadioButton>(R.id.light).isChecked = true
                }
                2 -> {
                    findViewById<RadioButton>(R.id.dark).isChecked = true
                }
                3 -> {
                    findViewById<RadioButton>(R.id.followSettings).isChecked = true
                }
            }
        }
        else
        {
            // Dark mode not supported
            findViewById<RadioButton>(R.id.followSettings).visibility = GONE
            when (spTheme.getInt("theme", 1)) {
                1 ->  {
                    findViewById<RadioButton>(R.id.light).isChecked = true
                }
                2 -> {
                    findViewById<RadioButton>(R.id.dark).isChecked = true
                }
            }
        }

        findViewById<EditText>(R.id.pin_code).setText(spPrivacy.getString("pinCode", ""))
        findViewById<Switch>(R.id.fp_unlock).isChecked = spPrivacy.getBoolean("biometricUnlock", false)
    }

    fun writeSettings()
    {
        if(findViewById<EditText>(R.id.pin_code).text.toString() == "" && findViewById<Switch>(R.id.fp_unlock).isChecked)
        {
            Toast.makeText(this, "Biometric unlock can't be enabled if a PIN code is not set", Toast.LENGTH_LONG).show()
            return
        }

        val spTweaks = getSharedPreferences("tweaks", Context.MODE_PRIVATE)
        val eTweaks = spTweaks.edit()
        val spTheme = getSharedPreferences("theme", Context.MODE_PRIVATE)
        val eTheme = spTheme.edit()
        val spPrivacy = getSharedPreferences("privacy", Context.MODE_PRIVATE)
        val ePrivacy = spPrivacy.edit()

        eTweaks.putBoolean("adBlock", findViewById<CheckBox>(R.id.block_ads).isChecked)
        eTweaks.putBoolean("oldProfile", findViewById<CheckBox>(R.id.old_profile_layout).isChecked)

        if(findViewById<RadioButton>(R.id.light).isChecked)
        {
            eTheme.putInt("theme", 1)
        }
        if(findViewById<RadioButton>(R.id.dark).isChecked)
        {
            eTheme.putInt("theme", 2)
        }
        if(findViewById<RadioButton>(R.id.followSettings).isChecked)
        {
            eTheme.putInt("theme", 3)
        }

        ePrivacy.putString("pinCode", findViewById<EditText>(R.id.pin_code).text.toString())
        ePrivacy.putBoolean("biometricUnlock", findViewById<Switch>(R.id.fp_unlock).isChecked)

        eTweaks.apply()
        eTheme.apply()
        ePrivacy.apply()

        Toast.makeText(this, "Settings saved, restart QWeb to apply changes.", Toast.LENGTH_LONG).show()
        finish()
    }
}