package com.flaviotps.mapeditor.data.loader

import com.flaviotps.mapeditor.data.map.RawTile
import com.flaviotps.mapeditor.data.map.TileSet
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

private const val TILES_FILE = "/config/tilesets.xml"
private const val TILESET = "tileset"
private const val NAME = "name"
private const val RAW = "raw"
private const val ID = "id"
private const val TYPE = "type"
class ResourceLoader {

    fun loadTiles(): MutableList<TileSet> {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()

        val inputStream = javaClass.getResourceAsStream(TILES_FILE)

        val doc = builder.parse(inputStream)
        doc.documentElement.normalize()

        val tileSets = mutableListOf<TileSet>()
        val tileSetNodes = doc.getElementsByTagName(TILESET)

        for (i in 0 until tileSetNodes.length) {
            val tileSetNode = tileSetNodes.item(i) as Element
            val name = tileSetNode.getAttribute(NAME)

            val rawList = mutableListOf<RawTile>()
            val rawNodes = tileSetNode.getElementsByTagName(RAW).item(0).childNodes
            for (j in 0 until rawNodes.length) {
                val node = rawNodes.item(j)
                if (node.nodeType == Element.ELEMENT_NODE) {
                    val itemNode = node as Element
                    val id = itemNode.getAttribute(ID).toInt()
                    val type = itemNode.getAttribute(TYPE).toString()
                    rawList.add(RawTile(id, type))
                }
            }

            tileSets.add(TileSet(name, rawList))
        }

        return tileSets
    }
}
