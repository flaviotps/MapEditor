package com.flaviotps.mapeditor.map

import com.flaviotps.mapeditor.data.map.Tile
import com.flaviotps.mapeditor.data.map.TileMap
import com.flaviotps.mapeditor.data.map.TileType
import javafx.scene.ImageCursor
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.robot.Robot
import javafx.scene.transform.Scale

const val GRID_SIZE = 10
const val CELL_SIZE = 32
const val ZOOM_LEVEL = 1.0

class MapGrid(
    private val mapCallbacks: MapCallbacks
) : Pane() {

    private var zoomLevel: Double = ZOOM_LEVEL
    private val map = TileMap()
    private val canvas = Canvas(GRID_SIZE * CELL_SIZE.toDouble(), GRID_SIZE * CELL_SIZE.toDouble())
    private val graphicsContext: GraphicsContext = canvas.graphicsContext2D

    init {
        canvas.width = GRID_SIZE * CELL_SIZE.toDouble()
        canvas.height = GRID_SIZE * CELL_SIZE.toDouble()
        children.add(canvas)
        handleZoom()
        handleDrawing()
        handleGrid()
        handleEnterCanvas()
    }

    private fun handleEnterCanvas() {
        canvas.addEventHandler(MouseEvent.MOUSE_ENTERED) {
            val cursorImage = mapCallbacks.onTileDraw()?.imageView?.image
            cursorImage?.let {
                val cursor = ImageCursor(cursorImage, it.width / 2, it.width / 2)
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

            // Update canvas size
            canvas.width = GRID_SIZE * CELL_SIZE.toDouble()
            canvas.height = GRID_SIZE * CELL_SIZE.toDouble()

            event.consume()
        }
    }

    private fun handleGrid() {
        for (gridX in 0 until GRID_SIZE) {
            for (gridY in 0 until GRID_SIZE) {
                graphicsContext.fillRect(
                    gridX * CELL_SIZE.toDouble(),
                    gridY * CELL_SIZE.toDouble(),
                    CELL_SIZE.toDouble(),
                    CELL_SIZE.toDouble()
                )
                graphicsContext.stroke = Color.LIGHTGRAY
                graphicsContext.lineWidth = 1.0
                graphicsContext.strokeLine(
                    gridX * CELL_SIZE.toDouble(),
                    gridY * CELL_SIZE.toDouble(),
                    (gridX + 1) * CELL_SIZE.toDouble(),
                    gridY * CELL_SIZE.toDouble()
                ) // horizontal line
                graphicsContext.strokeLine(
                    gridX * CELL_SIZE.toDouble(),
                    gridY * CELL_SIZE.toDouble(),
                    gridX * CELL_SIZE.toDouble(),
                    (gridY + 1) * CELL_SIZE.toDouble()
                ) // vertical line
            }
        }
    }

    private fun handleDrawing() {
        canvas.setOnMouseDragged { event ->
            drawTile(event)
        }
        canvas.setOnMouseClicked { event ->
            drawTile(event)
        }
    }

    private fun drawTile(event: MouseEvent) {
        val mouseX = event.x.toInt()
        val mouseY = event.y.toInt()
        val cellX = (mouseX / CELL_SIZE).coerceIn(0, GRID_SIZE - 1)
        val cellY = (mouseY / CELL_SIZE).coerceIn(0, GRID_SIZE - 1)

        mapCallbacks.onTileDraw()?.let { selectedTile ->
            val image = selectedTile.imageView.image
            val id = selectedTile.id
            val type = selectedTile.type
            val tileX = (cellX * CELL_SIZE).toDouble() - (image.width - CELL_SIZE)
            val tileY = (cellY * CELL_SIZE).toDouble() - (image.height - CELL_SIZE)
            val newTile = Tile(id, type, tileX, tileY, image)
            map.setTile(cellX, cellY, newTile)
        }
        reDrawMap()
    }

    private fun reDrawMap() {
        handleGrid()
        for (gridY in 0 until GRID_SIZE) {
            for (gridX in 0 until GRID_SIZE) {
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
