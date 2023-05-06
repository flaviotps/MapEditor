package com.flaviotps.mapeditor.data.map

import com.flaviotps.mapeditor.extensions.toCellPosition
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.sun.javafx.geom.Vec2d
import java.io.File

const val MAP_SIZE = 2048

class TileMap {


    private val map = Array(MAP_SIZE) { arrayOfNulls<MutableList<Tile?>>(MAP_SIZE) }

    fun save(selectedFile: File, gridOffset: Vec2d) {
        val mapArray = JsonArray()
        for (cellY in 0 until MAP_SIZE) {
            for (cellX in 0 until MAP_SIZE) {
                getTile(cellX, cellY)?.let { tileSqm ->
                    val tilesArray = JsonArray()
                    tileSqm.forEach { tile ->
                        tilesArray.add(JsonObject().apply {
                            addProperty("id", tile?.id)
                            addProperty("type", tile?.type)
                            addProperty("imageWidth", tile?.imageWidth)
                            addProperty("imageHeight", tile?.imageHeight)
                        })
                    }
                    val sqm = JsonObject().apply {
                        addProperty("x", cellX - gridOffset.x.toCellPosition())
                        addProperty("y", cellY - gridOffset.y.toCellPosition())
                        add("tiles", tilesArray)
                    }
                    mapArray.add(sqm)
                }
            }
        }

        val json = JsonObject().apply {
            addProperty("width", MAP_SIZE)
            addProperty("height", MAP_SIZE)
            addProperty("xOffSet", gridOffset.x)
            addProperty("yOffSet", gridOffset.y)
            add("map", mapArray)
        }
        selectedFile.writeText(json.toString())
    }

    fun new() {
        for (x in 0 until MAP_SIZE) {
            for (y in 0 until MAP_SIZE) {
                map[x][y] = null
            }
        }
    }

    fun load(selectedFile: File) {
        val jsonString = selectedFile.readText()
        val json = JsonParser.parseString(jsonString).asJsonObject

        val mapArray = json.getAsJsonArray("map")
        val xOffSet = json.get("xOffSet").asDouble
        val yOffSet = json.get("yOffSet").asDouble

        // Clear the existing map
        for (x in 0 until MAP_SIZE) {
            for (y in 0 until MAP_SIZE) {
                map[x][y] = null
            }
        }

        // Load the tiles from the JSON data
        mapArray.forEach { sqm ->
            val sqmObj = sqm.asJsonObject
            val x = sqmObj.get("x").asInt + xOffSet.toCellPosition()
            val y = sqmObj.get("y").asInt + yOffSet.toCellPosition()
            val tilesArray = sqmObj.getAsJsonArray("tiles")

            tilesArray.forEach { tileJson ->
                val tileObj = tileJson.asJsonObject
                val id = tileObj.get("id").asInt
                val type = tileObj.get("type").asString
                val imageWidth = tileObj.get("imageWidth").asDouble
                val imageHeight = tileObj.get("imageHeight").asDouble
                val tile = Tile(id, type, x, y, imageWidth, imageHeight)
                setTile(tile)
            }
        }
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
        tile: Tile
    ) {
        map[tile.x][tile.y]?.let { tileStack ->
            when (tile.type) {
                TileType.UNSTACKABLE.value -> {
                    setUnstackable(tileStack, tile)
                }

                TileType.GROUND_BORDER.value -> {
                    setGroundBorder(tileStack, tile)
                }

                TileType.GROUND.value -> {
                    setGround(tileStack, tile)
                }

                else -> {
                    tileStack.add(tile)
                }
            }
        } ?: run {
            map[tile.x][tile.y] = mutableListOf(tile)
        }
    }

    private fun setGround(
        tileStack: MutableList<Tile?>,
        newTile: Tile
    ) {
        newTile.layer?.let { tileStack[it] = newTile }
    }

    private fun setGroundBorder(
        tileStack: MutableList<Tile?>,
        newTile: Tile
    ) {
        newTile.layer?.let { tileStack[it] = newTile }
    }

    private fun setUnstackable(tileStack: MutableList<Tile?>, newTile: Tile) {
        val index = tileStack.indexOfFirst { it?.type.equals(TileType.UNSTACKABLE.value, true) }
        if (index != -1) {
            tileStack[index] = newTile
        } else {
            tileStack.add(newTile)
        }
    }

    fun getTile(x: Int, y: Int): MutableList<Tile?>? {
        if ((x in 0 until MAP_SIZE) && (y in 0 until MAP_SIZE)) {
            return map[x][y]
        }
        return null
    }
}