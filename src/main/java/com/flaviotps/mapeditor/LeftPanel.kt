package com.flaviotps.mapeditor
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Pane

class LeftPanel : Pane() {

    init {
        style = "-fx-background-color: blue;"
        prefWidth = 0.2 * 600 // Blue column occupies 20% of the screen
    }

    fun getColumnConstraints(): ColumnConstraints {
        val col = ColumnConstraints()
        col.percentWidth = 20.0
        return col
    }
}
