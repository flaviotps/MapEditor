package com.flaviotps.mapeditor.map

import com.flaviotps.mapeditor.MenuTile
import com.flaviotps.mapeditor.clearGrid
import com.flaviotps.mapeditor.data.map.Tile
import com.flaviotps.mapeditor.data.map.TileMap
import com.flaviotps.mapeditor.data.map.Vector2
import com.flaviotps.mapeditor.drawMap
import com.flaviotps.mapeditor.drawOutlineAt
import com.flaviotps.mapeditor.extensions.addTile
import com.flaviotps.mapeditor.extensions.cellX
import com.flaviotps.mapeditor.extensions.cellY
import com.flaviotps.mapeditor.extensions.onPositionChanged
import com.flaviotps.mapeditor.state.Events
import com.flaviotps.mapeditor.state.MouseState
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.ImageCursor
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.transform.Scale
import org.koin.java.KoinJavaComponent.inject

const val GRID_CELL_SIZE = 256
const val CELL_SIZE = 32
const val ZOOM_LEVEL = 1.0
const val DRAW_GRID_LINES = true

class MapGrid : Pane() {

    internal var zoomLevel: Double = ZOOM_LEVEL
    internal val map = TileMap()
    internal val canvas = Canvas(GRID_CELL_SIZE * CELL_SIZE.toDouble(), GRID_CELL_SIZE * CELL_SIZE.toDouble())
    internal val events: Events by inject(Events::class.java)
    internal var lastCursorPosition = Vector2()

    private val canvasTranslateXProperty = SimpleDoubleProperty(0.0)
    private val canvasTranslateYProperty = SimpleDoubleProperty(0.0)
    private var lastMouseX: Double = 0.0
    private var lastMouseY: Double = 0.0

    init {
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

    private fun handleInputs() {
        canvas.setOnMouseMoved { event ->
            val cellX = event.cellX()
            val cellY = event.cellY()
            onPositionChanged(cellX, cellY) { x, y ->
                canvas.clearGrid()
                canvas.drawMap(map)
                canvas.drawOutlineAt(x, y)
            }
        }
        canvas.setOnMouseDragged { event ->
            if (event.isPrimaryButtonDown) {
                val cellX = event.cellX()
                val cellY = event.cellY()
                handleMouseState(cellX, cellY)
                onPositionChanged(cellX, cellY) { x, y ->
                    canvas.clearGrid()
                    canvas.drawMap(map)
                    canvas.drawOutlineAt(x, y)
                }
            } else if (event.isSecondaryButtonDown) {
                val deltaX = (event.x - lastMouseX) * zoomLevel
                val deltaY = (event.y - lastMouseY) * zoomLevel
                lastMouseX = event.x
                lastMouseY = event.y
                canvasTranslateXProperty.set(canvasTranslateXProperty.get() + deltaX)
                canvasTranslateYProperty.set(canvasTranslateYProperty.get() + deltaY)
            }
            canvas.translateXProperty().bind(canvasTranslateXProperty)
            canvas.translateYProperty().bind(canvasTranslateYProperty)
        }
        canvas.setOnMousePressed { event ->
            if (event.isPrimaryButtonDown) {
                val cellX = event.cellX()
                val cellY = event.cellY()
                handleMouseState(cellX, cellY)
                canvas.clearGrid()
                canvas.drawMap(map)
                canvas.drawOutlineAt(cellX, cellY)
            } else if (event.isSecondaryButtonDown) {
                lastMouseX = event.x
                lastMouseY = event.y
            }
        }
    }

    private fun handleMouseState(cellX: Int, cellY: Int) {
        when (val mouseState = events.mouseState) {
            is MouseState.TextureSelected -> {
                addTile(mouseState.tile, cellX, cellY)
            }

            is MouseState.Eraser -> {
                map.removeLast(cellX, cellY)
            }

            else -> {}
        }
    }
}
