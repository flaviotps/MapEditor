import com.flaviotps.mapeditor.IMAGE_PATH_GRASS
import com.flaviotps.mapeditor.LeftPanel
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.stage.Stage

const val GRID_SIZE = 256
const val CELL_SIZE = 32

class HelloApplication : Application() {

    override fun start(primaryStage: Stage) {

        val root = GridPane()
        val leftPanel = LeftPanel()
        val scrollPane = ScrollPane(MapGrid(GRID_SIZE, CELL_SIZE, IMAGE_PATH_GRASS))
        scrollPane.isFitToWidth = true
        scrollPane.isFitToHeight = true

        // Add column constraints to grid panel
        root.columnConstraints.addAll(getTextureMenusConstrains(), getMapConstrains())
        // Add blue pane to first column
        root.add(leftPanel, 0, 0)

        // Add grid to pink pane in second column
        root.add(scrollPane, 1, 0)

        // Create scene
        val scene = Scene(root, 1024.0, 768.0)
        primaryStage.title = "MapEditor"
        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun getMapConstrains() = ColumnConstraints().apply { percentWidth = 80.0  }
    private fun getTextureMenusConstrains() = ColumnConstraints().apply { percentWidth = 20.0  }

}

fun main() {
    Application.launch(HelloApplication::class.java)
}
