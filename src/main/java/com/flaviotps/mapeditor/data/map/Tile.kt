package com.flaviotps.mapeditor.data.map

import javafx.scene.image.Image

data class Tile(val id: Int,
                val type: String,
                val x: Double,
                val y: Double,
                val image: Image)