package com.flaviotps.mapeditor.map

import com.flaviotps.mapeditor.IMAGE_PATH_DIRT
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane

class Grid(private val gridSize: Int, private val cellSize: Int, private val imagePath: String) {

    private val gridPane: GridPane = GridPane()
    private var currentScale = 1.0

    init {
        gridPane.padding = Insets(0.0)
        gridPane.hgap = 0.5
        gridPane.vgap = 0.5

        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                val imageView = if (i == 8 && j == 8) {
                    ImageView(Image(IMAGE_PATH_DIRT))
                } else {
                    ImageView(Image(imagePath))
                }
                imageView.fitWidth = cellSize.toDouble()
                imageView.fitHeight = cellSize.toDouble()
                gridPane.add(imageView, j, i)
            }
        }
    }

    fun getGridPane(): GridPane {
        return gridPane
    }

    fun zoomIn() {
        zoom(1.1)
    }

    fun zoomOut() {
        zoom(0.9)
    }

    private fun zoom(zoomFactor: Double) {
        // Get the center point of the grid
        val centerX = gridPane.width / 2
        val centerY = gridPane.height / 2
        val zoomOrigin = Point2D(centerX, centerY)

        // Limit the zoom range to prevent the images from becoming too small or too large
        val newScale = currentScale * zoomFactor
        if (newScale in 0.5..1.0) {
            currentScale = newScale

            // Adjust the translation of the grid to keep the center point fixed during the zoom
            gridPane.translateX = centerX - centerX * currentScale
            gridPane.translateY = centerY - centerY * currentScale

            // Adjust the scale of the grid to zoom in/out
            gridPane.scaleX = currentScale
            gridPane.scaleY = currentScale
        }
    }
}
