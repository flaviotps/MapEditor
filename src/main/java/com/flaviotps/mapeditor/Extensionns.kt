package com.flaviotps.mapeditor

import com.flaviotps.mapeditor.data.map.RawTile
import com.flaviotps.mapeditor.map.CELL_SIZE
import javafx.scene.canvas.Canvas
import javafx.scene.image.ImageView
import javafx.scene.paint.Color


internal fun RawTile.toMenuTile(imageView: ImageView) = MenuTile(id, type, imageView)

internal fun Canvas.drawOutlineAt(x : Double, y : Double) {
    this.graphicsContext2D?.apply {
        lineWidth = 1.0
        stroke = Color.YELLOW
        strokeRect(x, y, CELL_SIZE.toDouble(), CELL_SIZE.toDouble())
    }
}