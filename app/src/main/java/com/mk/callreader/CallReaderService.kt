package com.mk.callreader

import android.accessibilityservice.AccessibilityService
import android.provider.ContactsContract
import android.speech.tts.TextToSpeech
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.util.Locale

class CallReaderService :
    AccessibilityService(),
    TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech

    private var lastNumber = ""
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

        val packageName =
            event.packageName?.toString()
                ?: return

        val isPhoneApp =
            packageName.contains("dialer", true) ||
            packageName.contains("phone", true) ||
            packageName.contains("contacts", true)

        if (!isPhoneApp) {
            return
        }

        val root =
            rootInActiveWindow
                ?: return

        val screenText =
            readScreen(root)

        val number =
            findNumber(screenText)
                ?: return

        val now =
            System.currentTimeMillis()

        if (
            number == lastNumber &&
            now - lastSpeakTime < 5000
        ) {
            return
        }

        lastNumber = number

        val name =
            findContactName(number)

        val result =
            name ?: "Unknown number"

        speak(result)

        lastSpeakTime = now
    }

    private fun readScreen(
        node: AccessibilityNodeInfo
    ): String {

        val text =
            StringBuilder()

        node.text?.let {
            text.append(it)
            text.append(" ")
        }

        node.contentDescription?.let {
            text.append(it)
            text.append(" ")
        }

        for (
            i in 0 until node.childCount
        ) {

            val child =
                node.getChild(i)

            if (child != null) {

                text.append(
                    readScreen(child)
                )

                child.recycle()
            }
        }

        return text.toString()
    }

    private fun findNumber(
        text: String
    ): String? {

        val parts =
            text.split(
                " ",
                "\n"
            )

        for (part in parts) {

            val number =
                part.filter {
                    it.isDigit()
                }

            if (
                number.length == 10 &&
                number.first() in '6'..'9'
            ) {
                return number
            }

            if (
                number.length == 12 &&
                number.startsWith("91") &&
                number[2] in '6'..'9'
            ) {
                return number.substring(2)
            }
        }

        return null
    }

    private fun findContactName(
        number: String
    ): String? {

        val uri =
            ContactsContract.PhoneLookup
                .CONTENT_FILTER_URI
                .buildUpon()
                .appendPath(number)
                .build()

        val projection =
            arrayOf(
                ContactsContract.PhoneLookup.DISPLAY_NAME
            )

        contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->

            if (cursor.moveToFirst()) {

                return cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        ContactsContract.PhoneLookup.DISPLAY_NAME
                    )
                )
            }
        }

        return null
    }

    override fun onInit(
        status: Int
    ) {

        if (status == TextToSpeech.SUCCESS) {

            tts.language =
                Locale("en", "IN")
        }
    }

    private fun speak(
        text: String
    ) {

        if (::tts.isInitialized) {

            tts.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "MK_CALL_READER"
            )
        }
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
