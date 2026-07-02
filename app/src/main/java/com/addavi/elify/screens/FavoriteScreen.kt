package com.addavi.elify.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.addavi.elify.R
import com.addavi.elify.components.SongItem
import com.addavi.elify.navigate.Screens
import com.addavi.elify.player.PlayerViewModel
import com.addavi.elify.ui.theme.vazirFont
import com.addavi.elify.viewmodel.MenuItemSpec
import com.addavi.elify.viewmodel.MusicViewModel

@Composable
fun FavoriteScreen(
    viewModel: MusicViewModel,
    navController: NavController,
    playerViewModel: PlayerViewModel
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){

        val favorites by viewModel.favorites.collectAsState()
        val songs by viewModel.songs.collectAsState()

        val favoriteSongs = remember(songs, favorites) {
            songs.filter { song -> favorites.any { it.id == song.id } }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Favorites",
                    fontSize = 25.sp,
                    fontFamily = vazirFont,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            if (favoriteSongs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No songs added to favorites.",
                        fontFamily = vazirFont,
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favoriteSongs) { song ->
                        SongItem(
                            song = song,
                            menuItems = listOf(
                                (MenuItemSpec("play", "Play", R.drawable.play_fill_ico)),
                                (MenuItemSpec("share", "share", R.drawable.share_ico)),
                                    (MenuItemSpec("info", "info", R.drawable.info_ico)),
                                (MenuItemSpec("remove_favorite", "Remove Favorite", R.drawable.remove_favorite_ico , destructive = true))
                            ),
                            onAction = { it ->
                                when (it) {
                                    "remove_favorite" -> viewModel.removeFromFavorite(song)
                                    "play" -> {
                                        playerViewModel.setPlaylist(favoriteSongs)
                                        playerViewModel.playSong(song)
                                        navController.navigate(Screens.Player.route) {
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            },
                            onClick = {
                                playerViewModel.setPlaylist(favoriteSongs)
                                playerViewModel.playSong(song)
                                navController.navigate(Screens.Player.route) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

