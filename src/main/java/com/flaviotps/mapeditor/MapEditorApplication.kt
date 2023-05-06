package com.flaviotps.mapeditor

import com.flaviotps.mapeditor.data.map.Vector2
import com.flaviotps.mapeditor.di.koinModules
import com.flaviotps.mapeditor.map.MapGrid
import com.flaviotps.mapeditor.map.MouseEventListener
import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.koin.core.context.GlobalContext.startKoin
import java.io.File

const val SCENE_WIDTH = 1024.0
const val SCENE_HEIGHT = 768.0

class MapEditorApplication : Application(), MouseEventListener {

    private val root = BorderPane()
    private val texturesMenu = TexturesMenu()
    private val mapGrid = MapGrid(this)
    private val statusLabel1 = Label("Ready") // Add labels to display status
    private val statusLabel2 = Label("Coordinates")

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

        val openMenuItem = MenuItem("Open")
        openMenuItem.setOnAction {
            showLoadFileDialog(primaryStage)
        }

        val newMenuTile = MenuItem("New")
        newMenuTile.setOnAction {
            mapGrid.new()
        }

        val exitMenuItem = MenuItem("Exit")
        exitMenuItem.setOnAction {
            Platform.exit()
        }

        fileMenu.items.addAll(newMenuTile, openMenuItem, saveMenuItem, exitMenuItem)
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

        // Create a separate container for the status panel
        val statusPane = StackPane()
        statusPane.alignment = Pos.CENTER
        val statusBox = HBox(10.0) // Use an HBox to hold the labels
        statusBox.alignment = Pos.CENTER
        statusBox.children.addAll(statusLabel1, statusLabel2)
        statusPane.children.add(statusBox)

        // Add the status panel to the bottom of the root layout
        root.bottom = statusPane

        // Create the scene
        val scene = Scene(root, SCENE_WIDTH, SCENE_HEIGHT)
        primaryStage.title = "MapEditor"
        primaryStage.scene = scene
        primaryStage.show()
    }


    private var lastSaveDirectory: File? = null

    private fun showSaveFileDialog(primaryStage: Stage) {
        val fileChooser = FileChooser()
        fileChooser.title = "Save Map"

        // Set the file extension filter for JSON files
        val jsonExtensionFilter = FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json")
        fileChooser.extensionFilters.add(jsonExtensionFilter)

        // Set the initial directory if available
        lastSaveDirectory?.let { fileChooser.initialDirectory = it }

        val selectedFile = fileChooser.showSaveDialog(primaryStage)
        if (selectedFile != null) {
            mapGrid.save(selectedFile)
            showAlert(Alert.AlertType.INFORMATION, "File Saved", "The map was saved successfully.")

            // Update the last save directory
            lastSaveDirectory = selectedFile.parentFile
        }
    }

    private fun showAlert(alertType: Alert.AlertType, title: String, message: String) {
        val alert = Alert(alertType)
        alert.title = title
        alert.contentText = message
        alert.showAndWait()
    }

    private fun showLoadFileDialog(primaryStage: Stage) {
        val fileChooser = FileChooser()
        fileChooser.title = "Load Map"

        // Set the file extension filter for JSON files
        val jsonExtensionFilter = FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json")
        fileChooser.extensionFilters.add(jsonExtensionFilter)

        // Set the initial directory if available
        lastSaveDirectory?.let { fileChooser.initialDirectory = it }

        val selectedFile = fileChooser.showOpenDialog(primaryStage)
        if (selectedFile != null) {
            mapGrid.load(selectedFile)
            showAlert(Alert.AlertType.INFORMATION, "File Loaded", "The map was loaded successfully.")

            // Update the last save directory
            lastSaveDirectory = selectedFile.parentFile
        }
    }

    override fun onMouseMoved(position: Vector2) {
        statusLabel2.text = "x:${position.x}, y:${position.y}"
    }
}

fun main() {
    startKoin { modules(koinModules) }
    Application.launch(MapEditorApplication::class.java)
}
