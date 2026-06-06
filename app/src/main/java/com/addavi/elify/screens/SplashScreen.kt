package com.addavi.elify.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.addavi.elify.R
import com.addavi.elify.navigate.Screens
import com.addavi.elify.ui.theme.vazirFont
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController){
    val isDark = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        delay(1500)
        navController.navigate(Screens.Home.route){
            popUpTo(Screens.Splash.route){inclusive = true}
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color.Black else Color.White),
        contentAlignment = Alignment.BottomCenter
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(if (isDark) R.drawable.logo_dark else R.drawable.logo_light),
                contentDescription = "",
                modifier = Modifier.size(200.dp).clip(RoundedCornerShape(20.dp))
            )
        }
        Text(
            text = "Powered By Addavi",
            fontFamily = vazirFont,
            color = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
    }
}