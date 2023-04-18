package com.flaviotps.mapeditor.map

import javafx.scene.image.Image

interface OnMapDraw {
    fun onTileDraw(x : Int, y : Int) : Image?
}