package com.flaviotps.mapeditor

import com.flaviotps.mapeditor.state.Events
import com.flaviotps.mapeditor.state.MouseState
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import org.koin.java.KoinJavaComponent.inject

class RightPanel : Pane() {

    private val events : Events by inject(Events::class.java)

    init {
        this.style = "-fx-background-color: grey;"
        this.prefHeight = SCENE_HEIGHT * 0.3

        // Create a VBox to hold the buttons
        val buttonBox = VBox()

        // Create the image views and buttons for the buttons
        val newImage = Image("icons/eraser.png")
        val newButton = Button("Apagar", ImageView(newImage).apply {
            fitWidth = 16.0
            fitHeight = 16.0
        })
        newButton.setOnAction {
            events.mouseState = MouseState.Eraser
        }

        // Add the buttons to the VBox
        buttonBox.children.addAll(newButton)

        // Set the spacing and padding of the VBox
        buttonBox.spacing = 5.0
        buttonBox.padding = Insets(10.0)

        // Add the VBox to the com.flaviotps.mapeditor.RightPanel
        this.children.add(buttonBox)
    }
}
