package com.flaviotps.mapeditor.map

import com.flaviotps.mapeditor.data.map.Tile
import com.flaviotps.mapeditor.data.map.TileMap
import com.flaviotps.mapeditor.drawOutlineAt
import com.flaviotps.mapeditor.extensions.cellX
import com.flaviotps.mapeditor.extensions.cellY
import com.flaviotps.mapeditor.extensions.gridX
import com.flaviotps.mapeditor.extensions.gridY
import com.flaviotps.mapeditor.state.Events
import com.flaviotps.mapeditor.state.MouseState
import com.sun.javafx.geom.Vec2d
import javafx.scene.ImageCursor
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.transform.Scale
import org.koin.java.KoinJavaComponent.inject

const val GRID_CELL_SIZE = 64
const val CELL_SIZE = 32
const val ZOOM_LEVEL = 1.0
const val DRAW_GRID_LINES = true

class MapGrid : Pane() {

    private var zoomLevel: Double = ZOOM_LEVEL
    private val map = TileMap()
    private val gridPixelSize = GRID_CELL_SIZE * CELL_SIZE.toDouble()
    private val canvas = Canvas(gridPixelSize, gridPixelSize)
    private val graphicsContext: GraphicsContext = canvas.graphicsContext2D
    private val events: Events by inject(Events::class.java)
    private var lastCursorPosition = Vec2d(0.0, 0.0)

    init {
        canvas.width = gridPixelSize
        canvas.height = gridPixelSize
        children.add(canvas)
        handleZoom()
        handleInputs()
        clearGrid()
        handleEnterCanvas()
    }

    private fun handleEnterCanvas() {
        canvas.addEventHandler(MouseEvent.MOUSE_ENTERED) {
            val mouseState = events.mouseState
            if (mouseState is MouseState.TextureSelected) {
                val cursorImage = mouseState.tile.imageView.image
                val cursor = ImageCursor(cursorImage, cursorImage.width / 2, cursorImage.width / 2)
                canvas.cursor = cursor
            }
            if (mouseState is MouseState.Eraser) {
                val cursorImage = null
                val cursor = ImageCursor(cursorImage)
                canvas.cursor = cursor
            }
        }
    }

    private fun handleZoom() {
        canvas.setOnScroll { event ->
            val delta = event.deltaY / 1000.0
            zoomLevel += delta
            zoomLevel = zoomLevel.coerceIn(0.1, 10.0)
            val scale = Scale(zoomLevel, zoomLevel)
            canvas.transforms.setAll(scale)
            event.consume()
        }
    }

    private fun clearGrid() {
        graphicsContext.fillRect(0.0, 0.0, gridPixelSize, gridPixelSize)
        if (DRAW_GRID_LINES) {
            graphicsContext.stroke = Color.GRAY
            graphicsContext.lineWidth = 1.0
            // Draw vertical lines
            for (x in 0..GRID_CELL_SIZE) {
                val startX = x * CELL_SIZE.toDouble()
                val startY = 0.0
                val endY = gridPixelSize
                graphicsContext.strokeLine(startX, startY, startX, endY)
            }

            // Draw horizontal lines
            for (y in 0..GRID_CELL_SIZE) {
                val startX = 0.0
                val endX = gridPixelSize
                val startY = y * CELL_SIZE.toDouble()
                graphicsContext.strokeLine(startX, startY, endX, startY)
            }
        }
    }


    private var prevMouseX: Double = 0.0
    private var prevMouseY: Double = 0.0
    private var isMiddleButtonDown: Boolean = false


    private fun handleInputs() {
        canvas.setOnMouseMoved { event ->
            val cellX = event.cellX()
            val cellY = event.cellY()
            val gridX = event.gridX()
            val gridY = event.gridY()
            val newPosition = Vec2d(cellX.toDouble(), cellY.toDouble())
            if(lastCursorPosition != newPosition) {
                clearGrid()
                reDrawMap()
                canvas.drawOutlineAt(gridX, gridY)
                lastCursorPosition = newPosition
            }
        }
        canvas.setOnMouseDragged { event ->
            if (isMiddleButtonDown) {
                val deltaX = (event.x - prevMouseX)
                val deltaY = (event.y - prevMouseY)
                canvas.layoutX += deltaX
                canvas.layoutY += deltaY
                prevMouseX = event.x
                prevMouseY = event.y
            } else if (event.isPrimaryButtonDown) {
                addTile(event)
                val cellX = event.cellX()
                val cellY = event.cellY()
                val gridX = event.gridX()
                val gridY = event.gridY()
                val newPosition = Vec2d(cellX.toDouble(), cellY.toDouble())
                if(lastCursorPosition != newPosition) {
                    clearGrid()
                    reDrawMap()
                    canvas.drawOutlineAt(gridX, gridY)
                    lastCursorPosition = newPosition
                }
            }
        }
        canvas.setOnMousePressed { event ->
            if (event.button == MouseButton.MIDDLE) {
                isMiddleButtonDown = true
                prevMouseX = event.x
                prevMouseY = event.y
            } else if (event.isPrimaryButtonDown) {
                addTile(event)
            }
        }
        canvas.setOnMouseReleased { event ->
            if (event.button == MouseButton.MIDDLE) {
                isMiddleButtonDown = false
            }
        }
    }

    private fun addTile(event: MouseEvent) {
        val cellX = event.cellX()
        val cellY = event.cellY()
        when (val mouseState = events.mouseState) {
            is MouseState.TextureSelected -> {
                val menuTile = mouseState.tile
                val image = menuTile.imageView.image
                val id = menuTile.id
                val type = menuTile.type
                val gridX = (cellX * CELL_SIZE).toDouble() - (image.width - CELL_SIZE)
                val gridY = (cellY * CELL_SIZE).toDouble() - (image.height - CELL_SIZE)
                val newTile = Tile(id, type, gridX, gridY, image)
                map.setTile(cellX, cellY, newTile)
                reDrawMap()
            }

            is MouseState.Eraser -> {
                map.removeLast(cellX, cellY)
                reDrawMap()
            }

            else -> {}
        }
    }

    private fun reDrawMap() {
        for (gridY in 0 until GRID_CELL_SIZE) {
            for (gridX in 0 until GRID_CELL_SIZE) {
                map.getTile(gridY, gridX)?.let { tiles ->
                    tiles.forEach { tile ->
                        graphicsContext.drawImage(
                            tile.image,
                            tile.x,
                            tile.y,
                            tile.image.width,
                            tile.image.height
                        )
                    }
                }
            }
        }
    }
}
