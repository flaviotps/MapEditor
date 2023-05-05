package com.flaviotps.mapeditor

import com.flaviotps.mapeditor.data.loader.imageCache
import com.flaviotps.mapeditor.data.map.MAP_SIZE
import com.flaviotps.mapeditor.data.map.RawTile
import com.flaviotps.mapeditor.data.map.TileMap
import com.flaviotps.mapeditor.extensions.toCellPosition
import com.flaviotps.mapeditor.extensions.toGridPosition
import com.flaviotps.mapeditor.map.CELL_SIZE_PIXEL
import com.flaviotps.mapeditor.map.DRAW_GRID_LINES
import com.flaviotps.mapeditor.map.GRID_CELL_DISPLAY_COUNT
import com.flaviotps.mapeditor.state.Events
import com.flaviotps.mapeditor.state.MouseState
import com.sun.javafx.geom.Vec2d
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import org.koin.java.KoinJavaComponent.inject


internal fun RawTile.toMenuTile(imageView: ImageView) = MenuTile(id, type, imageView)

internal fun Canvas.drawOutlineAt(x: Int, y: Int, gridOffset: Vec2d) {
    val events: Events by inject(Events::class.java)
    val mouseState = events.mouseState
    this.graphicsContext2D?.apply {
        stroke = if (mouseState is MouseState.Eraser) {
            Color.RED
        } else {
            Color.YELLOW
        }
        lineWidth = 1.0
        strokeRect(
            x.toGridPosition() - gridOffset.x.toCellPosition().toGridPosition(),
            y.toGridPosition() - gridOffset.y.toCellPosition().toGridPosition(),
            CELL_SIZE_PIXEL.toDouble(),
            CELL_SIZE_PIXEL.toDouble()
        )
    }
}

internal fun Canvas.clearGrid() {
    graphicsContext2D.fillRect(0.0, 0.0, width, height)
    if (DRAW_GRID_LINES) {
        graphicsContext2D.stroke = Color.GRAY
        graphicsContext2D.lineWidth = 1.0
        // Draw vertical lines
        for (x in 0..GRID_CELL_DISPLAY_COUNT) {
            val startX = x * CELL_SIZE_PIXEL.toDouble()
            val startY = 0.0
            graphicsContext2D.strokeLine(startX, startY, startX, height)
        }

        // Draw horizontal lines
        for (y in 0..GRID_CELL_DISPLAY_COUNT) {
            val startX = 0.0
            val startY = y * CELL_SIZE_PIXEL.toDouble()
            graphicsContext2D.strokeLine(startX, startY, width, startY)
        }
    }
}

internal fun Canvas.drawMap(map: TileMap, gridOffset: Vec2d) {
    for (cellY in 0 until MAP_SIZE) {
        for (cellX in 0 until MAP_SIZE) {
            map.getTile(cellX, cellY)?.let { tiles ->
                tiles.forEach { tile ->
                   imageCache[tile.id]?.let { image ->
                       val x = tile.x.toGridPosition() - (image.width.minus(CELL_SIZE_PIXEL) / CELL_SIZE_PIXEL).toInt().toGridPosition() - gridOffset.x.toCellPosition().toGridPosition()
                       val y = tile.y.toGridPosition() - (image.height.minus(CELL_SIZE_PIXEL) / CELL_SIZE_PIXEL).toInt().toGridPosition() - gridOffset.y.toCellPosition().toGridPosition()
                       graphicsContext2D.drawImage(image, x, y, tile.imageWidth, tile.imageHeight)
                   }
                }
            }
        }
    }
}