package com.mk.callreader

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

object VoiceHelper {

    private var tts: TextToSpeech? = null

    fun speak(
        context: Context,
        text: String
    ) {

        if (tts == null) {

            tts =
                TextToSpeech(
                    context.applicationContext
                ) { status ->

                    if (
                        status ==
                        TextToSpeech.SUCCESS
                    ) {

                        tts?.language =
                            Locale("en", "IN")

                        tts?.speak(
                            text,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "MK_READER"
                        )
                    }
                }

        } else {

            tts?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "MK_READER"
            )
        }
    }
}
