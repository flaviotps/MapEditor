package com.flaviotps.mapeditor.data.map

import com.flaviotps.mapeditor.data.loader.imageCache
import com.flaviotps.mapeditor.extensions.getNonNull
import com.flaviotps.mapeditor.extensions.toCellPosition
import com.flaviotps.mapeditor.extensions.toGridPosition
import com.flaviotps.mapeditor.map.CELL_SIZE_PIXEL
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.sun.javafx.geom.Vec2d
import javafx.scene.canvas.Canvas
import javafx.scene.effect.ColorAdjust
import java.io.File

const val MAP_SIZE = 2048
const val MAX_LEVEL = 7
const val DARKNESS_DELTA = 0.2 // adjust this value to change the darkness effect

class TileMap {


    private var currentLevel = 0

    private val mapLevels by lazy {
        hashMapOf<Int, Array<Array<MutableList<Tile?>?>>>().apply {
            for (level in 0..MAX_LEVEL) {
                this[level] = Array(MAP_SIZE) {
                    arrayOfNulls(MAP_SIZE)
                }
            }
        }
    }

    fun map(level: Int = currentLevel): Array<Array<MutableList<Tile?>?>> {
        return mapLevels.getNonNull(level)
    }

    //TODO SAVE ALL LEVELS
    fun save(selectedFile: File, gridOffset: Vec2d) {
        val mapArray = JsonArray()
        for (cellY in 0 until MAP_SIZE) {
            for (cellX in 0 until MAP_SIZE) {
                getTile(cellX, cellY, currentLevel)?.let { tileSqm ->
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
                map()[x][y] = null
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
                map()[x][y] = null
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
        map()[cellX][cellY]?.let { tileList ->
            if (tileList.isNotEmpty()) {
                tileList.removeLast()
            }
        }
    }

    fun setTile(
        tile: Tile
    ) {
        map()[tile.x][tile.y]?.let { tileStack ->
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
            map()[tile.x][tile.y] = mutableListOf(tile)
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

    private fun getTile(x: Int, y: Int, level: Int): MutableList<Tile?>? {
        if ((x in 0 until MAP_SIZE) && (y in 0 until MAP_SIZE)) {
            return map(level)[x][y]
        }
        return null
    }

    fun setLevel(level: Int) {
        currentLevel = level
    }

    fun drawMap(canvas: Canvas, gridOffset: Vec2d) {
        for (level in 0..currentLevel) {
            for (cellY in 0 until MAP_SIZE) {
                for (cellX in 0 until MAP_SIZE) {
                    getTile(cellX, cellY, level)?.let { tiles ->
                        tiles.forEach { tile ->
                            tile?.let {
                                imageCache[it.id]?.let { image ->
                                    val imageWidthOffset =
                                        (image.width.minus(CELL_SIZE_PIXEL) / CELL_SIZE_PIXEL).toInt().toGridPosition()
                                    val imageHeightOffset =
                                        (image.height.minus(CELL_SIZE_PIXEL) / CELL_SIZE_PIXEL).toInt().toGridPosition()
                                    val x = it.x.toGridPosition() - imageWidthOffset - gridOffset.x.toCellPosition()
                                        .toGridPosition()
                                    val y = it.y.toGridPosition() - imageHeightOffset - gridOffset.y.toCellPosition()
                                        .toGridPosition()
                                    canvas.graphicsContext2D.apply {
                                        drawImage(image, x, y, it.imageWidth, it.imageHeight)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}