import com.flaviotps.mapeditor.map.Grid
import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.stage.Stage

const val GRID_SIZE = 100
const val CELL_SIZE = 32
const val IMAGE_PATH_GRASS = "file:/Users/flaviotps/Desktop/tile.png"

class HelloApplication : Application() {

    override fun start(primaryStage: Stage) {
        val root = GridPane()

        // Create column constraints
        val col1 = ColumnConstraints()
        col1.percentWidth = 20.0 // Blue column occupies 20% of the screen
        val col2 = ColumnConstraints()
        col2.percentWidth = 80.0 // Pink column occupies 80% of the screen

        // Add column constraints to grid pane
        root.columnConstraints.addAll(col1, col2)

        // Create blue pane
        val bluePane = Pane()
        bluePane.style = "-fx-background-color: blue;"
        root.add(bluePane, 0, 0) // Add blue pane to first column

        // Create pink pane
        val pinkPane = Pane()
        pinkPane.style = "-fx-background-color: pink;"
        pinkPane.padding = Insets(10.0)
        root.add(pinkPane, 1, 0) // Add pink pane to second column

        // Create and add grid to pink pane
        val grid = Grid(GRID_SIZE, CELL_SIZE, IMAGE_PATH_GRASS)
        pinkPane.children.add(grid.getGridPane())

        // Create scene
        val scene = Scene(root, 600.0, 400.0)
        primaryStage.title = "Two Columns"
        primaryStage.scene = scene
        primaryStage.show()
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}
