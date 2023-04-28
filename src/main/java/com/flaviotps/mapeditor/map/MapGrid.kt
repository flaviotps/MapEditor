package com.flaviotps.mapeditor.map

import com.flaviotps.mapeditor.data.map.Tile
import com.flaviotps.mapeditor.data.map.TileMap
import com.flaviotps.mapeditor.state.Events
import com.flaviotps.mapeditor.state.MouseState
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
    private val events : Events by inject(Events::class.java)

    init {
        canvas.width = gridPixelSize
        canvas.height = gridPixelSize
        children.add(canvas)
        handleZoom()
        handleDrawing()
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
        for (gridX in 0 until GRID_CELL_SIZE) {
            for (gridY in 0 until GRID_CELL_SIZE) {
                graphicsContext.fillRect(0.0, 0.0, gridPixelSize, gridPixelSize)
            }
        }
        if (DRAW_GRID_LINES) {
            drawGridLines()
        }
    }

    private fun drawGridLines() {
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


    private var prevMouseX: Double = 0.0
    private var prevMouseY: Double = 0.0
    private var isMiddleButtonDown: Boolean = false

    private fun handleDrawing() {
        canvas.setOnMouseDragged { event ->
            if (isMiddleButtonDown) {
                val deltaX = (event.x - prevMouseX)
                val deltaY = (event.y - prevMouseY)
                canvas.layoutX += deltaX
                canvas.layoutY += deltaY
                prevMouseX = event.x
                prevMouseY = event.y
            } else if (event.isPrimaryButtonDown) {
                drawTile(event)
            }
        }
        canvas.setOnMousePressed { event ->
            if (event.button == MouseButton.MIDDLE) {
                isMiddleButtonDown = true
                prevMouseX = event.x
                prevMouseY = event.y
            } else if (event.isPrimaryButtonDown) {
                drawTile(event)
            }
        }
        canvas.setOnMouseReleased { event ->
            if (event.button == MouseButton.MIDDLE) {
                isMiddleButtonDown = false
            }
        }
    }

    private fun drawTile(event: MouseEvent) {
        val mouseX = event.x.toInt()
        val mouseY = event.y.toInt()
        val cellX = (mouseX / CELL_SIZE).coerceIn(0, GRID_CELL_SIZE - 1)
        val cellY = (mouseY / CELL_SIZE).coerceIn(0, GRID_CELL_SIZE - 1)
        when (val mouseState = events.mouseState) {
            is MouseState.TextureSelected -> {
                val menuTile = mouseState.tile
                val image = menuTile.imageView.image
                val id = menuTile.id
                val type = menuTile.type
                val tileX = (cellX * CELL_SIZE).toDouble() - (image.width - CELL_SIZE)
                val tileY = (cellY * CELL_SIZE).toDouble() - (image.height - CELL_SIZE)
                val newTile = Tile(id, type, tileX, tileY, image)
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
        clearGrid()
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
