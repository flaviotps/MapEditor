package com.flaviotps.mapeditor.data.map

import javafx.scene.image.Image

data class Tile(
    val id: Int,
    val type: String,
    val x: Int,
    val y: Int,
    val image: Image,
    val imageWidth: Double,
    val imageHeight: Double
)