package com.flaviotps.mapeditor.data.loader

import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

data class TileSet(val name: String, val raw: List<RawTile>)
data class RawTile(val id: Int)

const val TILES_FILE = "/config/tilesets.xml"

class ResourceLoader {

    fun loadTiles(): MutableList<TileSet> {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()

        val inputStream = javaClass.getResourceAsStream(TILES_FILE)

        val doc = builder.parse(inputStream)
        doc.documentElement.normalize()

        val tileSets = mutableListOf<TileSet>()
        val tileSetNodes = doc.getElementsByTagName("tileset")

        for (i in 0 until tileSetNodes.length) {
            val tileSetNode = tileSetNodes.item(i) as Element
            val name = tileSetNode.getAttribute("name")

            val rawList = mutableListOf<RawTile>()
            val rawNodes = tileSetNode.getElementsByTagName("raw").item(0).childNodes
            for (j in 0 until rawNodes.length) {
                val node = rawNodes.item(j)
                if (node.nodeType == Element.ELEMENT_NODE) {
                    val itemNode = node as Element
                    val id = itemNode.getAttribute("id").toInt()
                    rawList.add(RawTile(id))
                }
            }

            tileSets.add(TileSet(name, rawList))
        }

        return tileSets
    }
}
