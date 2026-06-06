package com.addavi.elify.navigate

sealed class Screens(val route : String , val level: Int){
    object Home : Screens("home" , 1)
    object Splash : Screens("splash" , 0)
    object Player : Screens("player" , 0)
    object Setting : Screens("setting" , 0)

}


