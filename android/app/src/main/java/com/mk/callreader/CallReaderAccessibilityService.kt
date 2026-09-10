package com.mk.callreader

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.content.Intent
import android.provider.Settings
import android.telephony.PhoneNumberUtils

class CallReaderAccessibilityService :
    AccessibilityService() {

    private var lastNumber = ""

    override fun onAccessibilityEvent(
        event: AccessibilityEvent
    ) {

        if (
            event.eventType !=
            AccessibilityEvent.TYPE_VIEW_CLICKED
            &&
            event.eventType !=
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            &&
            event.eventType !=
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        ) {
            return
        }


        val root =
            rootInActiveWindow
                ?: return


        val number =
            findPhoneNumber(root)


        if (
            number.isNullOrEmpty()
        ) {
            return
        }


        /*
            Same number ko baar-baar
            speak nahi karna
        */

        if (
            number == lastNumber
        ) {
            return
        }

        lastNumber = number


        val name =
            ContactHelper.findName(
                this,
                number
            )


        val text =
            name ?: "Unknown number"


        val enabled =
            getSharedPreferences(
                "MK_SETTINGS",
                MODE_PRIVATE
            ).getBoolean(
                "VOICE_ENABLED",
                true
            )


        if (enabled) {

            VoiceHelper.speak(
                this,
                text
            )
        }
    }


    private fun findPhoneNumber(
        root: AccessibilityNodeInfo
    ): String? {

        val text =
            collectText(root)


        val cleaned =
            text.replace(
                "\n",
                " "
            )


        val words =
            cleaned.split(
                " "
            )


        for (word in words) {

            val candidate =
                word.filter {
                    it.isDigit() ||
                    it == '+' ||
                    it == '-'
                }


            if (
                candidate.length >= 10
                &&
                PhoneNumberUtils
                    .isGlobalPhoneNumber(
                        candidate
                    )
            ) {

                return candidate
            }
        }

        return null
    }


    private fun collectText(
        node: AccessibilityNodeInfo
    ): String {

        val builder =
            StringBuilder()


        node.text?.let {

            builder.append(
                it.toString()
            )

            builder.append(" ")

        }


        node.contentDescription?.let {

            builder.append(
                it.toString()
            )

            builder.append(" ")

        }


        for (
            i in 0 until node.childCount
        ) {

            val child =
                node.getChild(i)


            if (child != null) {

                builder.append(
                    collectText(child)
                )

                child.recycle()
            }
        }


        return builder.toString()
    }


    override fun onInterrupt() {

    }
}
