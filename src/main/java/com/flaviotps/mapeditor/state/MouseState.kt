package com.flaviotps.mapeditor.state

import com.flaviotps.mapeditor.MenuTile

sealed class MouseState {
    class TextureSelected(val tile : MenuTile) : MouseState()
    object Eraser : MouseState()
    object None : MouseState()

}

