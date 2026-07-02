package com.addavi.elify.navigate

sealed class Screens(val route : String , val level: Int){
    object Home : Screens("home" , 1)
    object Search : Screens("search" , 0)
    object Player : Screens("player" , 0)
    object Setting : Screens("setting" , 0)
    object Favorite : Screens("favorite" , 1)
    object DriveScreen : Screens("driveScreen" , 1)

}


