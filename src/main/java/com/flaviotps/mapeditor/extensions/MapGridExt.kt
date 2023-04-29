package com.flaviotps.mapeditor.extensions

import com.flaviotps.mapeditor.MenuTile
import com.flaviotps.mapeditor.data.map.Tile
import com.flaviotps.mapeditor.data.map.Vector2
import com.flaviotps.mapeditor.map.CELL_SIZE
import com.flaviotps.mapeditor.map.MapGrid
import kotlin.math.roundToInt

internal fun MapGrid.visibleTilesX(): Int {
    return (width / (CELL_SIZE * zoomLevel)).roundToInt()
}

internal fun MapGrid.visibleTilesY(): Int {
    return (height / (CELL_SIZE * zoomLevel)).roundToInt()
}

internal fun MapGrid.onPositionChanged(cellX: Int, cellY: Int, block: (x: Int, y: Int) -> Unit) {
    val newPosition = Vector2(cellX, cellY)
    if (lastCursorPosition != newPosition) {
        block(newPosition.x, newPosition.y)
        lastCursorPosition = newPosition
    }
}

internal fun MapGrid.addTile(
    tile: MenuTile,
    cellX: Int,
    cellY: Int
) {
    val image = tile.imageView.image
    val id = tile.id
    val type = tile.type
    val finalX = cellX - (image.width.minus(CELL_SIZE) / CELL_SIZE).toInt()
    val finalY = cellY - (image.width.minus(CELL_SIZE) / CELL_SIZE).toInt()
    val newTile = Tile(id, type, finalX, finalY, image, image.width, image.height)
    map.setTile(cellX, cellY, newTile)
}