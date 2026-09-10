package com.mk.callreader

import android.accessibilityservice.AccessibilityService
import android.speech.tts.TextToSpeech
import android.view.accessibility.AccessibilityEvent
import java.util.Locale

class CallReaderService :
    AccessibilityService(),
    TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech

    private var lastText = ""
    private var lastSpeakTime = 0L

    override fun onServiceConnected() {

        super.onServiceConnected()

        tts = TextToSpeech(
            applicationContext,
            this
        )
    }

    override fun onAccessibilityEvent(
        event: AccessibilityEvent
    ) {

        // Sirf Phone / Dialer app
        val packageName =
            event.packageName?.toString()
                ?: return

        val isPhoneApp =
            packageName.contains("dialer", true) ||
            packageName.contains("phone", true)

        if (!isPhoneApp) {
            return
        }

        // Sirf click
        if (
            event.eventType !=
            AccessibilityEvent.TYPE_VIEW_CLICKED
        ) {
            return
        }

        val text =
            event.text
                ?.joinToString(" ")
                ?.trim()
                ?: return

        if (text.isEmpty()) {
            return
        }

        // Sirf MK prefix wale text
        if (!text.startsWith("MK ", true)) {
            return
        }

        // "MK " hatao
        val name =
            text.substring(3).trim()

        if (name.isEmpty()) {
            return
        }

        val now =
            System.currentTimeMillis()

        // Same naam ko repeatedly mat bolo
        if (
            name.equals(lastText, true) &&
            now - lastSpeakTime < 3000
        ) {
            return
        }

        lastText = name
        lastSpeakTime = now

        speak(name)
    }

    override fun onInit(
        status: Int
    ) {

        if (
            status == TextToSpeech.SUCCESS
        ) {

            tts.language =
                Locale("en", "IN")
        }
    }

    private fun speak(
        text: String
    ) {

        if (!::tts.isInitialized) {
            return
        }

        tts.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "MK_CALL_READER"
        )
    }

    override fun onInterrupt() {

        if (::tts.isInitialized) {
            tts.stop()
        }
    }

    override fun onDestroy() {

        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }

        super.onDestroy()
    }
    }
