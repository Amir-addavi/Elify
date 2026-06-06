package com.addavi.elify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.shadow
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
            .padding(bottom = 8.dp)
            .width(300.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 15.dp,
                ambientColor = Color.Black,
                spotColor = Color.Black,
                shape = RoundedCornerShape(50.dp)
            )
            .clip(RoundedCornerShape(50.dp))
            .background(
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.98f)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BottomNavigationItem(NAV , R.drawable.home_ico , "Home" , Screens.Home.route)
        BottomNavigationItem(NAV , R.drawable.heart_ico , "Favorite" , "Screens.Home.route")
        BottomNavigationItem(NAV , R.drawable.play_list_ico , "PlayLists" , "Screens.Home.route")
        BottomNavigationItem(NAV , R.drawable.setting_ico , "Setting" , Screens.Setting.route)
    }
}

@Composable
fun BottomNavigationItem(
    navController: NavController,
    iconRes: Int,
    label: String,
    route: String
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selected = currentRoute == route

    Column(
        modifier = Modifier
            .width(70.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
            .clickable {
                navController.navigate(route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
            .padding(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = label,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = label,
            fontFamily = vazirFont,
            fontSize = 12.sp,
            lineHeight = 1.sp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onTertiary,
        )
    }
}
