package com.flaviotps.mapeditor

import com.flaviotps.mapeditor.di.koinModules
import com.flaviotps.mapeditor.map.MapGrid
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.koin.core.context.GlobalContext.startKoin

const val SCENE_WIDTH = 1024.0
const val SCENE_HEIGHT = 768.0

class MapEditorApplication : Application() {

    private val root = BorderPane()
    private val texturesMenu = TexturesMenu()
    private val mapGrid = MapGrid()
    override fun start(primaryStage: Stage) {
        // Create the MapGrid and ScrollPane

        val scrollPane = ScrollPane(mapGrid)
        scrollPane.isFitToWidth = true
        scrollPane.isFitToHeight = true

        // Create the menu bar
        val menuBar = MenuBar()
        val fileMenu = Menu("File")

        val saveMenuItem = MenuItem("Save")
        saveMenuItem.setOnAction {
            showSaveFileDialog(primaryStage)
        }

        fileMenu.items.addAll(MenuItem("New"), MenuItem("Open"), saveMenuItem, MenuItem("Exit"))
        val viewMenu = Menu("View")
        viewMenu.items.addAll(MenuItem("Zoom In"), MenuItem("Zoom Out"))
        val editMenu = Menu("Edit")
        editMenu.items.addAll(MenuItem("Cut"), MenuItem("Copy"), MenuItem("Paste"))
        menuBar.menus.addAll(fileMenu, viewMenu, editMenu)

        // Add the menu bar to the top of the root layout
        root.top = menuBar

        // Create a separate container for the left panel
        val leftPane = VBox()
        leftPane.children.addAll(texturesMenu, RightPanel())

        // Add the left panel to the left side of the root layout
        root.left = leftPane

        // Add the scroll pane to the center of the root layout
        root.center = scrollPane

        // Create the scene
        val scene = Scene(root, SCENE_WIDTH, SCENE_HEIGHT)
        primaryStage.title = "MapEditor"
        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun showSaveFileDialog(primaryStage: Stage) {
        val fileChooser = FileChooser()
        fileChooser.title = "Save Map"

        // Set the file extension filter for JSON files
        val jsonExtensionFilter = FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json")
        fileChooser.extensionFilters.add(jsonExtensionFilter)

        val selectedFile = fileChooser.showSaveDialog(primaryStage)
        if (selectedFile != null) {
            mapGrid.save(selectedFile)
            showAlert(Alert.AlertType.INFORMATION, "File Saved", "The map was saved successfully.")
        }
    }


    private fun showAlert(alertType: Alert.AlertType, title: String, message: String) {
        val alert = Alert(alertType)
        alert.title = title
        alert.contentText = message
        alert.showAndWait()
    }
}

fun main() {
    startKoin { modules(koinModules) }
    Application.launch(MapEditorApplication::class.java)
}
