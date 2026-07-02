package com.addavi.elify.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.addavi.elify.R
import com.addavi.elify.components.SongItem
import com.addavi.elify.components.premiumClick
import com.addavi.elify.navigate.Screens
import com.addavi.elify.player.PlayerViewModel
import com.addavi.elify.ui.theme.vazirFont
import com.addavi.elify.viewmodel.MenuItemSpec
import com.addavi.elify.viewmodel.MusicViewModel

@Composable
fun SearchScreen(
    viewModel: MusicViewModel,
    playerViewModel: PlayerViewModel,
    navController: NavController
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val favorites by viewModel.favorites.collectAsState()

    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.setSearchQuery("")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .premiumClick(
                        shape = 16,
                        scaleDown = 0.95f,
                        onClick = {
                            focusManager.clearFocus()
                            navController.popBackStack()
                        }
                    )
                , contentAlignment = Alignment.Center
            ){
                Icon(
                    painter = painterResource(R.drawable.back_left_ico),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .weight(1f),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 14.sp,
                        fontFamily = vazirFont,
                        textAlign = if (searchQuery.isRtl()) TextAlign.Right else TextAlign.Left,
                        textDirection = if (searchQuery.isRtl()) TextDirection.Rtl else TextDirection.Ltr
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (searchQuery.isRtl()) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search songs, artists, albums...",
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    fontSize = 16.sp,
                                    fontFamily = vazirFont,
                                    textAlign = TextAlign.Start
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                if (searchQuery.isNotEmpty()) {
                    Icon(
                        painter = painterResource(R.drawable.cancel_ico),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(17.dp)
                            .clickable { viewModel.setSearchQuery("") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            searchQuery.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Search for your favorite songs",
                        fontFamily = vazirFont,
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontSize = 16.sp
                    )
                }
            }
            searchResults.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results found",
                        fontFamily = vazirFont,
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontSize = 16.sp
                    )
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults) { song ->
                        SongItem(
                            song = song,
                            onClick = {
                                playerViewModel.setPlaylist(searchResults)
                                playerViewModel.playSong(song)
                                navController.navigate(Screens.Player.route)
                            },
                            menuItems = buildList {
                                add(MenuItemSpec("play", "Play", R.drawable.play_fill_ico))
                                add(MenuItemSpec("next_play", "Next Play", R.drawable.next_player_ico))
                                add(MenuItemSpec("share", "share", R.drawable.share_ico))
                                add(MenuItemSpec("info", "info", R.drawable.info_ico))
                                if (favorites.any { it.id == song.id })
                                    add(MenuItemSpec("remove_favorite", "Remove Favorite", R.drawable.remove_favorite_ico))
                                else
                                    add(MenuItemSpec("add_favorite", "Add Favorite", R.drawable.heart_ico))
                                add(MenuItemSpec("delete", "Delete", R.drawable.delete_ico, destructive = true))
                            },
                            onAction = {
                                when (it) {
                                    "add_favorite" -> viewModel.addToFavorite(song)
                                    "remove_favorite" -> viewModel.removeFromFavorite(song)
                                    "play" -> {
                                        playerViewModel.setPlaylist(searchResults)
                                        playerViewModel.playSong(song)
                                        navController.navigate(Screens.Player.route) {
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
fun String.isRtl(): Boolean {
    if (this.isEmpty()) return false
    val char = this.firstOrNull { it.isLetter() } ?: return false
    val directionality = Character.getDirectionality(char)
    return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
            directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
}