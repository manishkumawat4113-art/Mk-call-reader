package com.mk.callreader

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)

        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(40, 40, 40, 40)

        val title = TextView(this)

        title.text = "📞 MK Call Reader"
        title.textSize = 28f
        title.gravity = Gravity.CENTER

        val info = TextView(this)

        info.text =
            "\nPhone app ke saath MK Call Reader automatically kaam karega.\n\n" +
            "Pehle Contacts permission allow karo, " +
            "phir Accessibility Service ON karo."

        info.textSize = 17f
        info.gravity = Gravity.CENTER

        val contactButton = Button(this)

        contactButton.text = "Allow Contacts"

        contactButton.setOnClickListener {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_CONTACTS
                    ),
                    100
                )
            }
        }

        val accessibilityButton = Button(this)

        accessibilityButton.text =
            "Turn ON MK Call Reader"

        accessibilityButton.setOnClickListener {

            startActivity(
                Intent(
                    Settings.ACTION_ACCESSIBILITY_SETTINGS
                )
            )
        }

        layout.addView(title)
        layout.addView(info)
        layout.addView(contactButton)
        layout.addView(accessibilityButton)

        setContentView(layout)
    }
}
