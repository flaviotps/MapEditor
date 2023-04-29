package com.flaviotps.mapeditor.map

import com.flaviotps.mapeditor.clearGrid
import com.flaviotps.mapeditor.data.map.Tile
import com.flaviotps.mapeditor.data.map.TileMap
import com.flaviotps.mapeditor.drawMap
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
    private var prevMouseX: Double = 0.0
    private var prevMouseY: Double = 0.0
    private var isMiddleButtonDown: Boolean = false

    init {
        canvas.width = gridPixelSize
        canvas.height = gridPixelSize
        children.add(canvas)
        handleZoom()
        handleInputs()
        canvas.clearGrid()
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

    private fun MouseEvent.onPositionChanged(block: () -> Unit) {
        val cellX = cellX()
        val cellY = cellY()
        val newPosition = Vec2d(cellX.toDouble(), cellY.toDouble())
        if (lastCursorPosition != newPosition) {
            block()
            lastCursorPosition = newPosition
        }
    }

    private fun handleInputs() {
        canvas.setOnMouseMoved { event ->
            val gridX = event.gridX()
            val gridY = event.gridY()
            event.onPositionChanged {
                canvas.clearGrid()
                canvas.drawMap(map)
                canvas.drawOutlineAt(gridX, gridY)
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
                val gridX = event.gridX()
                val gridY = event.gridY()
                event.onPositionChanged {
                    canvas.clearGrid()
                    canvas.drawMap(map)
                    canvas.drawOutlineAt(gridX, gridY)
                }
            }
        }
        canvas.setOnMousePressed { event ->
            val gridX = event.gridX()
            val gridY = event.gridY()
            if (event.button == MouseButton.MIDDLE) {
                isMiddleButtonDown = true
                prevMouseX = event.x
                prevMouseY = event.y
            } else if (event.isPrimaryButtonDown) {
                addTile(event)
                canvas.clearGrid()
                canvas.drawMap(map)
                canvas.drawOutlineAt(gridX, gridY)
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
                val gridX = event.gridX() - image.width.minus(CELL_SIZE)
                val gridY = event.gridY() - image.height.minus(CELL_SIZE)
                val newTile = Tile(id, type, gridX, gridY, image)
                map.setTile(cellX, cellY, newTile)
            }

            is MouseState.Eraser -> {
                map.removeLast(cellX, cellY)
            }

            else -> {}
        }
    }
}
