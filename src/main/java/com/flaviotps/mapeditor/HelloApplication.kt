import com.flaviotps.mapeditor.LeftPanel
import com.flaviotps.mapeditor.map.Grid
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.stage.Stage

const val GRID_SIZE = 100
const val CELL_SIZE = 32
const val IMAGE_PATH_GRASS = "file:/Users/flaviotps/Desktop/tile.png"

class HelloApplication : Application() {

    override fun start(primaryStage: Stage) {
        val root = GridPane()

        val leftPanel = LeftPanel()
        val rightPanel = RightPanel()

        // Add column constraints to grid panel
        root.columnConstraints.addAll(leftPanel.getColumnConstraints(), rightPanel.getColumnConstraints())
        // Add blue pane to first column
        root.add(leftPanel, 0, 0)

        // Add grid to pink pane in second column
        val grid = Grid(GRID_SIZE, CELL_SIZE, IMAGE_PATH_GRASS)
        rightPanel.children.add(grid.getGridPane())
        root.add(rightPanel, 1, 0)

        // Create scene
        val scene = Scene(root, 600.0, 400.0)
        primaryStage.title = "MapEditor"
        primaryStage.scene = scene
        primaryStage.show()
    }

}

fun main() {
    Application.launch(HelloApplication::class.java)
}
