package com.addavi.elify.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.addavi.elify.R
import com.addavi.elify.ui.theme.vazirFont

@Composable
fun SettingScreen(darkMode: Boolean , onDarkModeChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        Column(
            modifier = Modifier.fillMaxSize()
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
                    text = "Setting",
                    fontSize = 25.sp,
                    fontFamily = vazirFont,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            LazyColumn {
                items(count = 1){it
                    Spacer(modifier = Modifier.height(20.dp))
                    SettingItem(R.drawable.theme_ico , "Theme & Colors") { onDarkModeChange(!darkMode) }
                    SettingItem(R.drawable.language_ico , "Language & Region") {}
                }
            }
        }
    }
}

@Composable
fun SettingItem(icon: Int , label: String , onClick: () -> Unit){
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            )
            .padding(vertical = 10.dp , horizontal = 12.dp)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    .padding(6.dp)
            )
            Text(
                text = label,
                fontFamily = vazirFont,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
        Icon(
            painter = painterResource(R.drawable.arrow_right_ico),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier.size(23.dp)
        )
    }
}