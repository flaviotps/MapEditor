package com.flaviotps.mapeditor.extensions

import com.flaviotps.mapeditor.map.CELL_SIZE
import com.flaviotps.mapeditor.map.GRID_CELL_SIZE
import javafx.scene.input.MouseEvent

internal fun MouseEvent.cellX(): Int {
    return  (this.x.toInt() / CELL_SIZE).coerceIn(0, GRID_CELL_SIZE - 1)
}

internal fun MouseEvent.cellY(): Int {
    return  (this.y.toInt() / CELL_SIZE).coerceIn(0, GRID_CELL_SIZE - 1)
}

internal fun MouseEvent.gridX(): Double {
    return (cellX() * CELL_SIZE).toDouble()
}

internal fun MouseEvent.gridY(): Double {
    return (cellY() * CELL_SIZE).toDouble()
}
