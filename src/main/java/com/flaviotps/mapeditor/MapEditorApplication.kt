package com.flaviotps.mapeditor

import com.flaviotps.mapeditor.map.MapGrid
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.stage.Stage



class MapEditorApplication : Application() {

    private val root = GridPane()
    private val leftPanel = LeftPanel()

    override fun start(primaryStage: Stage) {

        val mapGrid = MapGrid(mapCallbacks = leftPanel)
        val scrollPane = ScrollPane(mapGrid)
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

    private fun getMapConstrains() = ColumnConstraints().apply { percentWidth = 85.0 }
    private fun getTextureMenusConstrains() = ColumnConstraints().apply { percentWidth = 20.0 }
}

fun main() {
    Application.launch(MapEditorApplication::class.java)
}
