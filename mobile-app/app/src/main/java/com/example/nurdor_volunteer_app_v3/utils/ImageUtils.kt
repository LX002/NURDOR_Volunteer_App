package com.example.nurdor_volunteer_app_v3.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

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

        fun loadImageIntoIntoImageView(img: String?, replacementResource: Int, imageView: ImageView, context: Context) {
            val mimeType: String? = img?.let { getMimeType(img) }

            Glide.with(context)
                .asBitmap()
                .load(mimeType?.let { "data:$mimeType;base64,$img" } ?: replacementResource)
                .override(250, 250)
                .centerCrop()
                .into(imageView)
        }
    }
}