package com.nin0dev.qweb

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.view.View.GONE
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        readSettings()
        findViewById<Button>(R.id.saveSettings).setOnClickListener {
            writeSettings()
        }
    }

    fun readSettings()
    {
        val spTweaks = getSharedPreferences("tweaks", Context.MODE_PRIVATE)
        val spTheme = getSharedPreferences("theme", Context.MODE_PRIVATE)
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
    }

    fun writeSettings()
    {
        val spTweaks = getSharedPreferences("tweaks", Context.MODE_PRIVATE)
        val eTweaks = spTweaks.edit()
        val spTheme = getSharedPreferences("theme", Context.MODE_PRIVATE)
        val eTheme = spTheme.edit()

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

        eTweaks.apply()
        eTheme.apply()

        Toast.makeText(this, "Settings saved, restart QWeb to apply changes.", Toast.LENGTH_LONG).show()
        finish()
    }
}