package com.addavi.elify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.addavi.elify.R
import com.addavi.elify.navigate.Screens
import com.addavi.elify.ui.theme.vazirFont

@Composable
fun BottomNavigation(NAV : NavController) {
    Row(
        modifier = Modifier
            .navigationBarsPadding()
            .width(330.dp)
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .width(265.dp)
                .fillMaxWidth()
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.98f)
                )
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BottomNavigationItem(
                NAV,
                R.drawable.home_ico,
                R.drawable.home_fill_ico,
                "Home",
                Screens.Home.route
            )
            BottomNavigationItem(
                NAV,
                R.drawable.favorite_ico,
                R.drawable.favorite_fill_ico,
                "Favorite",
                Screens.Favorite.route
            )
            BottomNavigationItem(
                NAV,
                R.drawable.playlist_ico,
                R.drawable.playlist_fill_ico,
                "PlayLists",
                "Screens.Home.route"
            )
            BottomNavigationItem(
                NAV,
                R.drawable.setting_ico,
                R.drawable.setting_fill_ico,
                "Setting",
                Screens.Setting.route
            )
        }
        Spacer(modifier = Modifier.width(2.dp))
        Box(
            modifier = Modifier
                .size(55.dp)
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(RoundedCornerShape(20.dp))
                .premiumClick(
                    shape = 20,
                    rippleColor = Color.Transparent,
                    scaleDown = 0.85f,
                    onClick = { NAV.navigate(Screens.Search.route) }
                )
                .background(MaterialTheme.colorScheme.onBackground),
            contentAlignment = Alignment.Center
        ){
            Icon(
                painter = painterResource(R.drawable.search_ico),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.inversePrimary,
                modifier = Modifier
                    .size(22.dp)
            )
        }
    }
}

@Composable
fun BottomNavigationItem(
    navController: NavController,
    iconRes: Int,
    activeIcon: Int,
    label: String,
    route: String
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selected = currentRoute == route

    Column(
        modifier = Modifier
            .width(62.dp)
            .height(55.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
            .premiumClick(
                shape = 20,
                scaleDown = 0.85f,
                rippleColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(Screens.Home.route) {
                                saveState = true
                                inclusive = false
                            }
                        }
                    }
                }
            )
            .padding(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(if (selected) activeIcon else iconRes),
            contentDescription = label,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontFamily = vazirFont,
            fontSize = 10.sp,
            lineHeight = 1.sp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary,
        )
    }
}
