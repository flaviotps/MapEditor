package com.flaviotps.mapeditor.map
import com.flaviotps.mapeditor.data.map.Tile
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.transform.Scale

class MapGrid(
    private val gridSize: Int,
    private val cellSize: Int,
    private var zoomLevel: Double = 1.0
) : Pane() {

    private lateinit var onMapDraw: OnMapDraw
    private val tileMap = Array(gridSize) { arrayOfNulls<Tile?>(gridSize) }

    fun setOnMapDrawListener(onMapDraw: OnMapDraw) { this.onMapDraw = onMapDraw }

    private val canvas = Canvas(gridSize * cellSize.toDouble(), gridSize * cellSize.toDouble())
    private val graphicsContext: GraphicsContext = canvas.graphicsContext2D

    init {
        canvas.width = gridSize * cellSize.toDouble()
        canvas.height = gridSize * cellSize.toDouble()
        children.add(canvas)

        handleZoom()
        handleDrawing()
        handleGrid()
    }

    private fun handleZoom() {
        canvas.setOnScroll { event ->
            val delta = event.deltaY / 1000.0
            zoomLevel += delta
            zoomLevel = zoomLevel.coerceIn(0.1, 10.0)
            val scale = Scale(zoomLevel, zoomLevel)
            canvas.transforms.setAll(scale)

            // Update canvas size
            canvas.width = gridSize * cellSize.toDouble()
            canvas.height = gridSize * cellSize.toDouble()

            event.consume()
        }
    }

    private fun handleGrid() {
        for (gridX in 0 until gridSize) {
            for (gridY in 0 until gridSize) {
                if (tileMap[gridX][gridY] == null) {
                    graphicsContext.fillRect(
                        gridX * cellSize.toDouble(),
                        gridY * cellSize.toDouble(),
                        cellSize.toDouble(),
                        cellSize.toDouble()
                    )
                    graphicsContext.stroke = Color.LIGHTGRAY
                    graphicsContext.lineWidth = 1.0
                    graphicsContext.strokeLine(
                        gridX * cellSize.toDouble(),
                        gridY * cellSize.toDouble(),
                        (gridX + 1) * cellSize.toDouble(),
                        gridY * cellSize.toDouble()
                    ) // horizontal line
                    graphicsContext.strokeLine(
                        gridX * cellSize.toDouble(),
                        gridY * cellSize.toDouble(),
                        gridX * cellSize.toDouble(),
                        (gridY + 1) * cellSize.toDouble()
                    ) // vertical line
                }
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
        val cellX = (mouseX / cellSize).coerceIn(0, gridSize - 1)
        val cellY = (mouseY / cellSize).coerceIn(0, gridSize - 1)
        onMapDraw.onTileDraw(cellX, cellY)?.let { image ->
            val tileX = (cellX * cellSize).toDouble() - (image.width - cellSize)
            val tileY = (cellY * cellSize).toDouble() - (image.height - cellSize)
            graphicsContext.drawImage(image, tileX, tileY, image.width, image.height)
        }
        tileMap[cellX][cellY] = Tile(1, cellX, cellY)
    }
}
