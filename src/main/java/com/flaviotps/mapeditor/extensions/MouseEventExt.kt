package com.flaviotps.mapeditor.extensions

import com.flaviotps.mapeditor.map.CELL_SIZE_PIXEL
import com.flaviotps.mapeditor.map.GRID_CELL_COUNT
import javafx.scene.input.MouseEvent
import kotlin.math.roundToInt

internal fun MouseEvent.cellX(): Int {
    return  (this.x.toInt() / CELL_SIZE_PIXEL).coerceIn(0, GRID_CELL_COUNT - 1)
}

internal fun MouseEvent.cellY(): Int {
    return  (this.y.toInt() / CELL_SIZE_PIXEL).coerceIn(0, GRID_CELL_COUNT - 1)
}

internal fun MouseEvent.gridX(): Double {
    return (cellX() * CELL_SIZE_PIXEL).toDouble()
}

internal fun Int.toGridPosition() : Double {
    return (this * CELL_SIZE_PIXEL).toDouble()
}

internal fun Double.toCellPosition() : Int{
    return (this/CELL_SIZE_PIXEL).toInt()
}

internal fun MouseEvent.gridY(): Double {
    return (cellY() * CELL_SIZE_PIXEL).toDouble()
}
