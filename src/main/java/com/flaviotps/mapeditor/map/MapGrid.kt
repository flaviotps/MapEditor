package com.flaviotps.mapeditor.map

import com.flaviotps.mapeditor.clearGrid
import com.flaviotps.mapeditor.data.map.MAP_SIZE
import com.flaviotps.mapeditor.data.map.TileMap
import com.flaviotps.mapeditor.data.map.Vector2
import com.flaviotps.mapeditor.drawOutlineAt
import com.flaviotps.mapeditor.extensions.*
import com.flaviotps.mapeditor.state.Events
import com.flaviotps.mapeditor.state.MouseState
import com.sun.javafx.geom.Vec2d
import javafx.scene.ImageCursor
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.transform.Scale
import org.koin.java.KoinJavaComponent.inject
import java.io.File


const val GRID_CELL_DISPLAY_COUNT = 256
const val CELL_SIZE_PIXEL = 32
const val ZOOM_LEVEL = 1.0

const val DRAG_SENSIBILITY = 10.0

class MapGrid(private val mouseEventListener: MouseEventListener) : Pane() {

    internal var drawGrid = false
    internal var zoomLevel: Double = ZOOM_LEVEL
    internal val map = TileMap()
    private val canvas = Canvas(
        GRID_CELL_DISPLAY_COUNT * CELL_SIZE_PIXEL.toDouble(),
        GRID_CELL_DISPLAY_COUNT * CELL_SIZE_PIXEL.toDouble()
    )
    private val events: Events by inject(Events::class.java)
    internal var lastCursorPosition = Vector2()
    private var gridOffset = Vec2d(
        ((MAP_SIZE / 2) * CELL_SIZE_PIXEL).toDouble(),
        ((MAP_SIZE / 2) * CELL_SIZE_PIXEL).toDouble()
    )
    private var lastMouseX: Double = 0.0
    private var lastMouseY: Double = 0.0

    init {
        children.add(canvas)
        handleZoom()
        handleInputs()
        canvas.clearGrid(drawGrid)
        handleEnterCanvas()
    }

    fun save(selectedFile: File) = map.save(selectedFile, gridOffset)
    fun load(selectedFile: File) = map.load(selectedFile)
    fun new() = map.new()
    fun setLevel(level: Int) {
        map.setLevel(level)
        canvas.clearGrid(drawGrid)
        map.drawMap(canvas, gridOffset, visibleTilesX(), visibleTilesY())
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
            if (visibleTilesX() < GRID_CELL_DISPLAY_COUNT && visibleTilesY() < GRID_CELL_DISPLAY_COUNT) {
                zoomLevel = zoomLevel.coerceIn(0.1, 10.0)
                val scale = Scale(zoomLevel, zoomLevel)
                canvas.transforms.setAll(scale)
                event.consume()
            } else {
                zoomLevel -= delta
            }
        }
    }

    private fun handleInputs() {
        canvas.setOnMouseMoved { event ->
            val cellX = event.cellX() + gridOffset.x.toCellPosition()
            val cellY = event.cellY() + gridOffset.y.toCellPosition()
            onPositionChanged(cellX, cellY) { x, y ->
                mouseEventListener.onMouseMoved(Vector2(x, y))
                canvas.clearGrid(drawGrid)
                map.drawMap(canvas, gridOffset, visibleTilesX(), visibleTilesY())
                canvas.drawOutlineAt(x, y, gridOffset)
            }
        }
        canvas.setOnMouseDragged { event ->
            if (event.isPrimaryButtonDown) {
                val cellX = event.cellX() + gridOffset.x.toCellPosition()
                val cellY = event.cellY() + gridOffset.y.toCellPosition()
                handleMouseState(cellX, cellY)
                onPositionChanged(cellX, cellY) { x, y ->
                    canvas.clearGrid(drawGrid)
                    map.drawMap(canvas, gridOffset, visibleTilesX(), visibleTilesY())
                    canvas.drawOutlineAt(x, y, gridOffset)
                }
            } else if (event.isSecondaryButtonDown) {
                val deltaX = (lastMouseX - event.x) * zoomLevel * DRAG_SENSIBILITY
                val deltaY = (lastMouseY - event.y) * zoomLevel * DRAG_SENSIBILITY
                gridOffset.x += deltaX
                gridOffset.y += deltaY
                lastMouseX = event.x
                lastMouseY = event.y
            }
        }
        canvas.setOnMousePressed { event ->
            if (event.isPrimaryButtonDown) {
                val cellX = event.cellX() + gridOffset.x.toCellPosition()
                val cellY = event.cellY() + gridOffset.y.toCellPosition()
                handleMouseState(cellX, cellY)
                canvas.clearGrid(drawGrid)
                map.drawMap(canvas, gridOffset, visibleTilesX(), visibleTilesY())
                canvas.drawOutlineAt(cellX, cellY, gridOffset)
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
