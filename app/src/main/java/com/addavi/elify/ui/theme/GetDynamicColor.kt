package com.addavi.elify.ui.theme

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@RequiresApi(Build.VERSION_CODES.O)
suspend fun extractDominantColorFromUri(
    context: Context,
    uri: Uri?
): Color {

    return withContext(Dispatchers.IO) {

        try {

            val loader = ImageLoader(context)

            val request = ImageRequest.Builder(context)
                .data(uri)
                .size(64)
                .allowHardware(false)
                .build()

            val result = loader.execute(request)

            val bitmap = (result.drawable as BitmapDrawable)
                .bitmap
            val palette = Palette
                .from(bitmap)
                .clearFilters()
                .generate()

            Color(
                palette.getDominantColor(
                    android.graphics.Color.argb(50f , 117f ,232f , 0f)
                )
            )

        } catch (e: Exception) {

            Color(0xFF000000)

        }

    }
}
