package com.flaviotps.mapeditor.state

import com.flaviotps.mapeditor.MenuTile

sealed class MouseState {
    class TextureSelected(val selectedTile : MenuTile) : MouseState()
    object Eraser : MouseState()
    object None : MouseState()

}

