package com.flaviotps.mapeditor.map

import javafx.geometry.Insets
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.TilePane
import javafx.geometry.Bounds
import javafx.geometry.Rectangle2D
import javafx.scene.Node
import javafx.stage.Screen

class Grid(
    private val gridSize: Int,
    private val cellSize: Int,
    private val imagePath: String,
    private var zoomLevel: Double = 1.0
) : TilePane() {

    init {
        prefColumns = gridSize
        padding = Insets(0.0)
      /*  hgap = 0.05
        vgap = 0.05*/

        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                val imageView = ImageView(Image(imagePath))
                imageView.fitWidth = cellSize.toDouble()
                imageView.fitHeight = cellSize.toDouble()
                children.add(imageView)
            }
        }

        setOnScroll { event ->
            if (event.deltaY > 0) {
                zoomLevel += 0.1
            } else {
                zoomLevel -= 0.1
            }
            updateZoom()
            event.consume()
        }
    }

    private fun updateZoom() {
        children.forEach {
            it.scaleX = zoomLevel
            it.scaleY = zoomLevel
        }
        prefTileWidth = cellSize * zoomLevel
        prefTileHeight = cellSize * zoomLevel
        if (zoomLevel > 0) {
            val rows = (gridSize / zoomLevel).toInt()
            val cols = (gridSize / zoomLevel).toInt()
            prefRows = if (rows > 0) rows else 1
            prefColumns = if (cols > 0) cols else 1
        } else {
            prefRows = gridSize
            prefColumns = gridSize
        }
    }
}
