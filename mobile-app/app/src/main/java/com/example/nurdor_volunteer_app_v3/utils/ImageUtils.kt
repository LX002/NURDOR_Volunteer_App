package com.example.nurdor_volunteer_app_v3.utils

class ImageUtils {

    companion object {
        fun getMimeType(eventImg: String): String? {
            val typeIdentifier = eventImg.take(15)
            return when {
                typeIdentifier.startsWith("iVBORw0KGgo") -> "image/png"
                typeIdentifier.startsWith("/9j/") -> "image/jpeg"
                typeIdentifier.startsWith("R0lGODdh") -> "image/gif"
                else -> null
            }
        }
    }
}