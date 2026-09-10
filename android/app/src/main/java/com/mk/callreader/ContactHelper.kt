package com.mk.callreader

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat

object ContactHelper {

    fun findName(
        context: Context,
        phoneNumber: String
    ): String? {

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        val uri =
            ContactsContract.PhoneLookup
                .CONTENT_FILTER_URI
                .buildUpon()
                .appendPath(phoneNumber)
                .build()

        val projection =
            arrayOf(
                ContactsContract.PhoneLookup.DISPLAY_NAME
            )

        context.contentResolver.query(
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
}
