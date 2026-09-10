package com.mk.callreader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

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
            "\nPhone app me contact name ke aage MK likho.\n\n" +
            "Example:\n" +
            "MK Rahul\n" +
            "MK Papa\n" +
            "MK Mohit\n\n" +
            "MK wale naam par tap karne par naam bola jayega."

        info.textSize = 17f
        info.gravity = Gravity.CENTER

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
        layout.addView(accessibilityButton)

        setContentView(layout)
    }
}
