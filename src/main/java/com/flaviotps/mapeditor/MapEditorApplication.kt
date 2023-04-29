package com.flaviotps.mapeditor

import com.flaviotps.mapeditor.di.koinModules
import com.flaviotps.mapeditor.map.MapGrid
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import javafx.stage.Stage
import org.koin.core.context.GlobalContext.startKoin

const val SCENE_WIDTH = 1024.0
const val SCENE_HEIGHT = 768.0

class MapEditorApplication : Application() {

    private val root = BorderPane()
    private val texturesMenu = TexturesMenu()

    override fun start(primaryStage: Stage) {
        // Create the MapGrid and ScrollPane
        val mapGrid = MapGrid()
        val scrollPane = ScrollPane(mapGrid)
        scrollPane.isFitToWidth = true
        scrollPane.isFitToHeight = true

        // Create the menu bar
        val menuBar = MenuBar()
        val fileMenu = Menu("File")
        fileMenu.items.addAll(MenuItem("New"), MenuItem("Open"), MenuItem("Save"), MenuItem("Exit"))
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
}

fun main() {
    startKoin { modules(koinModules) }
    Application.launch(MapEditorApplication::class.java)
}
