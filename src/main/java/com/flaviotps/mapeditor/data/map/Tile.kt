package com.flaviotps.mapeditor.data.map

data class Tile(
    val id: Int,
    val type: String,
    val x: Int,
    val y: Int,
    val imageWidth: Double,
    val imageHeight: Double,
    val layer  : Int? = null
)