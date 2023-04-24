package com.flaviotps.mapeditor.data.map

import com.flaviotps.mapeditor.map.GRID_CELL_SIZE

class TileMap {

    private val map = Array(GRID_CELL_SIZE) { arrayOfNulls<MutableList<Tile>>(GRID_CELL_SIZE) }

    fun removeLast(
        cellX: Int,
        cellY: Int
    ) {
        map[cellX][cellY]?.let { tileList ->
            if (tileList.isNotEmpty()) {
                tileList.removeLast()
            }
        }
    }

    fun setTile(
        cellX: Int,
        cellY: Int,
        newTile: Tile
    ) {
        map[cellX][cellY]?.let { tileStack ->
            when (newTile.type) {
                TileType.UNSTACKABLE.value -> {
                    setUnstackable(tileStack, newTile)
                }
                TileType.GROUND.value -> {
                    setGround(tileStack, newTile)
                }
                else -> {
                    tileStack.add(newTile)
                }
            }
        } ?: run {
            map[cellX][cellY] = mutableListOf(newTile)
        }
    }

    private fun setGround(
        tileStack: MutableList<Tile>,
        newTile: Tile
    ) {
        if (tileStack.isNotEmpty() && tileStack.first().type.equals(TileType.GROUND.value, true)) {
            tileStack[0] = newTile
        } else {
            tileStack.add(0, newTile)
        }
    }

    private fun setUnstackable(
        tileStack: MutableList<Tile>,
        newTile: Tile
    ) {
        tileStack.forEachIndexed { index, tile ->
            if (tile.type.equals(TileType.UNSTACKABLE.value, true)) {
                tileStack[index] = newTile
                return
            }
        }
        tileStack.add(newTile)
    }

    fun getTile(x: Int, y: Int): MutableList<Tile>? {
        return map[x][y]
    }
}