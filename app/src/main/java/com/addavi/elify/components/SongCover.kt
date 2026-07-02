package com.addavi.elify.components

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.addavi.elify.R
@Composable
fun SongCover(
    albumId: Long,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    val imageUri = remember(albumId) {
        Uri.parse("content://media/external/audio/albumart/$albumId")
    }

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUri)
            .crossfade(true)
            .memoryCacheKey(albumId.toString())
            .diskCacheKey(albumId.toString())
            .build(),
        contentDescription = null,
        error = painterResource(R.drawable.default_cover),
        fallback = painterResource(R.drawable.default_cover),
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}
