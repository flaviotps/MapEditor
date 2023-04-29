package com.flaviotps.mapeditor

import com.flaviotps.mapeditor.data.map.RawTile
import com.flaviotps.mapeditor.data.map.TileMap
import com.flaviotps.mapeditor.extensions.toGridPosition
import com.flaviotps.mapeditor.map.CELL_SIZE
import com.flaviotps.mapeditor.map.DRAW_GRID_LINES
import com.flaviotps.mapeditor.map.GRID_CELL_SIZE
import javafx.scene.canvas.Canvas
import javafx.scene.image.ImageView
import javafx.scene.paint.Color


internal fun RawTile.toMenuTile(imageView: ImageView) = MenuTile(id, type, imageView)

internal fun Canvas.drawOutlineAt(x: Int, y: Int) {
    this.graphicsContext2D?.apply {
        lineWidth = 1.0
        stroke = Color.YELLOW
        strokeRect(x.toGridPosition(), y.toGridPosition(), CELL_SIZE.toDouble(), CELL_SIZE.toDouble())
    }
}

internal fun Canvas.clearGrid() {
    graphicsContext2D.fillRect(0.0, 0.0, width, height)
    if (DRAW_GRID_LINES) {
        graphicsContext2D.stroke = Color.GRAY
        graphicsContext2D.lineWidth = 1.0
        // Draw vertical lines
        for (x in 0..GRID_CELL_SIZE) {
            val startX = x * CELL_SIZE.toDouble()
            val startY = 0.0
            graphicsContext2D.strokeLine(startX, startY, startX, height)
        }

        // Draw horizontal lines
        for (y in 0..GRID_CELL_SIZE) {
            val startX = 0.0
            val startY = y * CELL_SIZE.toDouble()
            graphicsContext2D.strokeLine(startX, startY, width, startY)
        }
    }
}

internal fun Canvas.drawMap(map: TileMap) {
    for (cellX in 0 until GRID_CELL_SIZE) {
        for (cellY in 0 until GRID_CELL_SIZE) {
            map.getTile(cellX, cellY)?.let { tiles ->
                tiles.forEach { tile ->
                    graphicsContext2D.drawImage(
                        tile.image,
                        tile.x.toGridPosition(),
                        tile.y.toGridPosition(),
                        tile.image.width,
                        tile.image.height
                    )
                }
            }
        }
    }
}