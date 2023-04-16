import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.scene.transform.Scale

class MapGrid(
    private val gridSize: Int,
    private val cellSize: Int,
    private val imagePath: String,
    private var zoomLevel: Double = 1.0
) : Pane() {

    private val canvas = Canvas(gridSize * cellSize.toDouble(), gridSize * cellSize.toDouble())
    private val graphicsContext: GraphicsContext = canvas.graphicsContext2D

    init {
        children.add(canvas)

        val image = Image(imagePath)

        canvas.setOnScroll { event ->
            val delta = event.deltaY / 1000.0
            zoomLevel += delta
            zoomLevel = zoomLevel.coerceIn(0.1, 10.0)
            val scale = Scale(zoomLevel, zoomLevel)
            canvas.transforms.setAll(scale)
            event.consume()
        }

        canvas.setOnMouseDragged { event ->
            val x = event.x.toInt()
            val y = event.y.toInt()
            val cellX = (x / cellSize).coerceIn(0, gridSize - 1)
            val cellY = (y / cellSize).coerceIn(0, gridSize - 1)
            graphicsContext.fillRect(cellX * cellSize.toDouble(), cellY * cellSize.toDouble(), cellSize.toDouble(), cellSize.toDouble())
        }

        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                graphicsContext.drawImage(image, i * cellSize.toDouble(), j * cellSize.toDouble(), cellSize.toDouble(), cellSize.toDouble())
            }
        }
    }
}
