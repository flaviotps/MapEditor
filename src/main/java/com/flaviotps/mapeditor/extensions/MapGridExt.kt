package com.flaviotps.mapeditor.extensions

import com.flaviotps.mapeditor.map.CELL_SIZE
import com.flaviotps.mapeditor.map.MapGrid
import kotlin.math.roundToInt

internal fun MapGrid.visibleTilesX(): Int {
    return (width / (CELL_SIZE * zoomLevel)).roundToInt()
}

internal fun MapGrid.visibleTilesY(): Int {
    return (height / (CELL_SIZE * zoomLevel)).roundToInt()
}