package com.addavi.elify.ui.theme

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun extractDominantColorFromUri(
    context: Context,
    uri: Uri?
): Color {
    return withContext(Dispatchers.IO) {
        try {
            val loader = ImageLoader(context)

            val request = ImageRequest.Builder(context)
                .data(uri)
                .size(150) // سایز بزرگتر برای دقت بهتر
                .allowHardware(false)
                .build()

            val result = loader.execute(request)
            val bitmap = (result.drawable as BitmapDrawable).bitmap

            val palette = Palette.from(bitmap)
                .clearFilters()
                .maximumColorCount(32) // بیشتر رنگ بررسی کن
                .generate()

            // اولویت‌بندی سواچ‌ها برای رنگ‌های زنده‌تر
            val swatch = palette.vibrantSwatch
                ?: palette.lightVibrantSwatch
                ?: palette.darkVibrantSwatch
                ?: palette.dominantSwatch
                ?: palette.mutedSwatch

            val color = swatch?.rgb?.let { Color(it) } ?: Color(0xFF1DE57F)

            // اگه رنگ خیلی تیره یا خیلی کم‌کنتراست بود، روشن‌ترش کن
            adjustColorForVisibility(color)

        } catch (e: Exception) {
            Color(0xFF1DE57F) // رنگ پیش‌فرض جذاب به جای سیاه
        }
    }
}

fun adjustColorForVisibility(color: Color): Color {
    val hsl = FloatArray(3)
    android.graphics.Color.colorToHSV(color.toArgb(), hsl)

    // اگه brightness خیلی کم بود (تیره یا مشکی)، بالا ببر
    if (hsl[2] < 0.35f) {
        hsl[2] = 0.55f
    }

    // اگه saturation خیلی کم بود (خاکستری)، بالا ببر تا رنگ‌تر بشه
    if (hsl[1] < 0.3f) {
        hsl[1] = 0.5f
    }

    return Color(android.graphics.Color.HSVToColor(hsl))
}


fun getContrastingIconColor(backgroundColor: Color): Color {
    val hsl = FloatArray(3)
    android.graphics.Color.colorToHSV(backgroundColor.toArgb(), hsl)

    // اگه پس‌زمینه روشن بود، آیکون تیره باشه و برعکس
    return if (hsl[2] > 0.6f) {
        Color.Black.copy(alpha = 0.85f)
    } else {
        Color.White.copy(alpha = 0.9f)
    }
}
