package com.addavi.elify.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.addavi.elify.R
import com.addavi.elify.components.SongItem
import com.addavi.elify.components.SongItemSkeleton
import com.addavi.elify.components.SuggestionMusics
import com.addavi.elify.components.premiumClick
import com.addavi.elify.navigate.Screens
import com.addavi.elify.player.PlayerViewModel
import com.addavi.elify.tools.WindowSize
import com.addavi.elify.tools.rememberWindowSize
import com.addavi.elify.ui.theme.vazirFont
import com.addavi.elify.viewmodel.MenuItemSpec
import com.addavi.elify.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MusicViewModel, PVM: PlayerViewModel, navController: NavController) {

    val songs by viewModel.songs.collectAsStateWithLifecycle()
    val songCount by viewModel.songCount.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsState()

    val windowSize = rememberWindowSize()
    val columns = when (windowSize) {
        WindowSize.Compact -> 1
        WindowSize.Medium -> 2
        WindowSize.Expanded -> 3
    }

    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        0.2f to MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {

        LazyColumn(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Header(songCount, viewModel, navController, PVM, isLoading)
            }

            if (isLoading) {
                items(8) {
                    SongItemSkeleton()
                }
            } else {

                items(
                    items = songs,
                    key = { it.id }
                ) { song ->
                    SongItem(
                        song = song,
                        menuItems = buildList {
                            add(MenuItemSpec("play", "Play", R.drawable.play_fill_ico))
                            add(MenuItemSpec("next_play", "Next Play", R.drawable.next_player_ico))
                            add(MenuItemSpec("share", "share", R.drawable.share_ico))
                            add(MenuItemSpec("info", "info", R.drawable.info_ico))
                            if (favorites.any { it.id == song.id })
                                add(
                                    MenuItemSpec(
                                        "remove_favorite",
                                        "Remove Favorite",
                                        R.drawable.remove_favorite_ico
                                    )
                                )
                            else
                                add(
                                    MenuItemSpec(
                                        "add_favorite",
                                        "Add Favorite",
                                        R.drawable.heart_ico
                                    )
                                )
                            add(
                                MenuItemSpec(
                                    "delete",
                                    "Delete",
                                    R.drawable.delete_ico,
                                    destructive = true
                                )
                            )
                        },
                        onAction = { it ->
                            when (it) {
                                "add_favorite" -> viewModel.addToFavorite(song)
                                "remove_favorite" -> viewModel.removeFromFavorite(song)
                                "play" -> {
                                    PVM.setPlaylist(songs)
                                    PVM.playSong(song)
                                    navController.navigate(Screens.Player.route) {
                                        launchSingleTop = true
                                    }
                                }
                            }
                        },
                        onClick = {
                            PVM.setPlaylist(songs)
                            PVM.playSong(song)
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



    @Composable
    fun SortCard(Id: Int, name: String, Icon: Int) {
        Box(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .premiumClick(
                    shape = 10,
                    scaleDown = 0.9f,
                    onClick = {}
                )
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(10.dp)
                )
                .shadow(
                    elevation = 12.dp,
                    spotColor = if (Id == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    ambientColor = if (Id == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(10.dp)
                )
                .clip(RoundedCornerShape(10.dp))
                .background(if (Id == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                .padding(vertical = 5.dp, horizontal = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(Icon),
                    contentDescription = null,
                    tint = if (Id == 1) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(25.dp)
                )
                Text(
                    text = name,
                    fontFamily = vazirFont,
                    fontSize = 17.sp,
                    color = if (Id == 1) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    }

    @Composable
    fun Header(
        songCount: Int,
        viewModel: MusicViewModel,
        navController: NavController,
        playerViewModel: PlayerViewModel,
        loading: Boolean
    ) {

        Column(
            modifier = Modifier


        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = if (loading) "Elify..." else "Elify",
                        fontFamily = vazirFont,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = if (loading) "loading all music from library..." else "What music are you thinking of today?",
                        fontFamily = vazirFont,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.offset(y = (-12).dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .premiumClick(
                            shape = 10,
                            scaleDown = 0.9f,
                            onClick = {}
                        )
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(com.addavi.elify.R.drawable.bell_ico),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.surface)
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (!loading) {
                Text(
                    text = "For you:",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontFamily = vazirFont,
                    modifier = Modifier.padding(5.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                SuggestionMusics(viewModel, playerViewModel, navController)
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Sort By:",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontFamily = vazirFont,
                    modifier = Modifier.padding(5.dp)
                )
                LazyRow {
                    item { SortCard(1, "All(${songCount})", com.addavi.elify.R.drawable.list_ico) }
                    item { SortCard(2, "Name", com.addavi.elify.R.drawable.name_list_ico) }
                    item { SortCard(3, "Time", com.addavi.elify.R.drawable.time_list_ico) }
                    item { SortCard(4, "Adding", com.addavi.elify.R.drawable.adding_list_ico) }
                    item { SortCard(5, "Artist", com.addavi.elify.R.drawable.artist_list_ico) }
                    item { SortCard(6, "Random", com.addavi.elify.R.drawable.random_list_ico) }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }


