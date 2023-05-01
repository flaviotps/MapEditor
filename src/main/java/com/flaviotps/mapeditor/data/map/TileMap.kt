package com.flaviotps.mapeditor.data.map

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.File

const val MAP_SIZE = 2048

class TileMap {


    private val map = Array(MAP_SIZE) { arrayOfNulls<MutableList<Tile>>(MAP_SIZE) }

    fun save(selectedFile: File) {
        val mapArray = JsonArray()
        for (cellX in 0 until MAP_SIZE) {
            for (cellY in 0 until MAP_SIZE) {
                getTile(cellX, cellY)?.let { tileSqm ->
                    val tilesArray = JsonArray()
                    tileSqm.forEach { tile ->
                        tilesArray.add(JsonObject().apply {
                            addProperty("id", tile.id)
                            addProperty("type", tile.type)
                            addProperty("imageWidth", tile.imageWidth)
                            addProperty("imageHeight", tile.imageHeight)
                        })
                    }
                    val sqm = JsonObject().apply {
                        addProperty("x", cellX)
                        addProperty("y", cellY)
                        add("tiles", tilesArray)
                    }
                    mapArray.add(sqm)
                }
            }
        }

        val json = JsonObject().apply {
            addProperty("width", MAP_SIZE)
            addProperty("height", MAP_SIZE)
            add("map", mapArray)
        }
        selectedFile.writeText(json.toString())
    }

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
        if ((x in 0 until MAP_SIZE) && (y in 0 until MAP_SIZE)) {
            return map[x][y]
        }
        return null
    }
}