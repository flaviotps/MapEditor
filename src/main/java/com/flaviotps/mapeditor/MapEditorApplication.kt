package com.flaviotps.mapeditor

import com.flaviotps.mapeditor.di.koinModules
import com.flaviotps.mapeditor.map.MapGrid

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.RowConstraints
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.koin.core.context.GlobalContext.startKoin


const val SCENE_WIDTH = 1024.0
const val SCENE_HEIGHT = 768.0

class MapEditorApplication : Application() {

    private val root = GridPane()
    private val texturesMenu = TexturesMenu()

    override fun start(primaryStage: Stage) {
        // Create the MapGrid and ScrollPane

        startKoin {
            modules(koinModules)
        }

        val mapGrid = MapGrid()
        val scrollPane = ScrollPane(mapGrid)
        scrollPane.isFitToWidth = true
        scrollPane.isFitToHeight = true

        val vBox = VBox()
        vBox.children.add(0, texturesMenu)
        vBox.children.add(1, RightPanel())
        // Add the left panel to the first column
        root.add(vBox, 0, 0)

        // Add the scroll pane to the second column
        root.add(scrollPane, 1, 0)

        // Set the column and row constraints to make the second column and first row fill the remaining space
        root.columnConstraints.addAll(
            ColumnConstraints().apply { percentWidth = 20.0 },
            ColumnConstraints().apply { percentWidth = 80.0 }
        )
        root.rowConstraints.addAll(
            RowConstraints().apply { percentHeight = 100.0 }
        )

        // Create the scene
        val scene = Scene(root, SCENE_WIDTH, SCENE_HEIGHT)
        primaryStage.title = "MapEditor"
        primaryStage.scene = scene
        primaryStage.show()
    }


}

fun main() {
    Application.launch(MapEditorApplication::class.java)
}
