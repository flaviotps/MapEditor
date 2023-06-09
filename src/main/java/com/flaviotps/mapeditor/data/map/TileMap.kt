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
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.io.File

const val MAP_SIZE = 2048
const val MAX_LEVEL = 10
const val DRAW_LOWER_LEVEL = true
const val CELL_DRAW_BUFFER = 10

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
        for (level in 0 until MAX_LEVEL) {
            for (cellY in 0 until MAP_SIZE) {
                for (cellX in 0 until MAP_SIZE) {
                    getTile(cellX, cellY, level)?.let { tileSqm ->
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
                            addProperty("level", level)
                            add("tiles", tilesArray)
                        }
                        mapArray.add(sqm)
                    }
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
        for (level in 0 until MAX_LEVEL) {
            for (x in 0 until MAP_SIZE) {
                for (y in 0 until MAP_SIZE) {
                    map(level)[x][y] = null
                }
            }
        }

        // Load the tiles from the JSON data
        mapArray.forEach { sqm ->
            val sqmObj = sqm.asJsonObject
            val x = sqmObj.get("x").asInt + xOffSet.toCellPosition()
            val y = sqmObj.get("y").asInt + yOffSet.toCellPosition()
            val level = sqmObj.get("level").asInt
            val tilesArray = sqmObj.getAsJsonArray("tiles")

            tilesArray.forEach { tileJson ->
                val tileObj = tileJson.asJsonObject
                val id = tileObj.get("id").asInt
                val type = tileObj.get("type").asString
                val imageWidth = tileObj.get("imageWidth").asDouble
                val imageHeight = tileObj.get("imageHeight").asDouble
                val tile = Tile(id, type, x, y, imageWidth, imageHeight)
                setTile(tile, level)
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
        tile: Tile,
        level: Int = currentLevel
    ) {
        map(level)[tile.x][tile.y]?.let { tileStack ->
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
            map(level)[tile.x][tile.y] = mutableListOf(tile)
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

    private fun startLevel(): Int {
        return if (currentLevel in 1..MAX_LEVEL && DRAW_LOWER_LEVEL) {
            currentLevel - 1
        } else {
            return currentLevel
        }
    }

    private fun brightness(level: Int): Double {
        return if (level == currentLevel) {
            0.0
        } else {
            -0.5
        }
    }

    private fun getFilteredImageView(image: Image, brightness: Double): ImageView {
        val colorAdjust = ColorAdjust()
        colorAdjust.brightness = brightness
        val filteredImage = ImageView(image)
        filteredImage.effect = colorAdjust
        return filteredImage
    }

    fun drawMap(canvas: Canvas, gridOffset: Vec2d, visibleX: Int, visibleY: Int) {
        val startX = gridOffset.x.toCellPosition() - visibleX - CELL_DRAW_BUFFER
        val startY = gridOffset.y.toCellPosition() - visibleY - CELL_DRAW_BUFFER
        val endX = gridOffset.x.toCellPosition() + visibleX + CELL_DRAW_BUFFER
        val endY = gridOffset.y.toCellPosition() + visibleY + CELL_DRAW_BUFFER
        println("rendering from x $startX to $endX")
        println("rendering from y $startY to $endY")
        val startLevel = startLevel()
        for (level in startLevel..currentLevel) {
            for (cellY in startX until endX) {
                for (cellX in startY until endY) {
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
                                    val filteredImageView = getFilteredImageView(image, brightness(level))
                                    canvas.graphicsContext2D.drawImage(
                                        filteredImageView.snapshot(null, null),
                                        x, y, it.imageWidth, it.imageHeight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}