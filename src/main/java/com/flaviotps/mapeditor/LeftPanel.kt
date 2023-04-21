package com.flaviotps.mapeditor

import com.flaviotps.mapeditor.data.loader.ResourceLoader
import com.flaviotps.mapeditor.map.MapCallbacks
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.TilePane
import javafx.scene.layout.VBox


private const val TEXTURE_DISPLAY_SIZE = 32.0

class MenuTile(var id: Int, var imageView: ImageView)

class LeftPanel : VBox(), MapCallbacks {

    private var selectedTile: MenuTile? = null
    private val tilePane = TilePane()
    private val menu = Menu("Textures")
    private val menuBar = MenuBar()
    private val resourceLoader = ResourceLoader()
    private var menuItems = mutableMapOf<MenuItem, List<MenuTile>>()

    init {
        style = "-fx-background-color: grey;"
        prefWidth = 0.2 * 600

        resourceLoader.loadTiles().forEach {
            val menuItem = MenuItem(it.name)
            val tiles = it.raw.map { rawTile -> MenuTile(rawTile.id, ImageView(Image("/image/${rawTile.id}.png"))) }
            menuItems[menuItem] = tiles
            menu.items.add(menuItem)
            menuItem.setOnAction { updateImageList(menuItem) }
        }


        menuBar.menus.add(menu)

        tilePane.padding = Insets(1.0)
        tilePane.hgap = 1.0
        tilePane.vgap = 1.0
        tilePane.alignment = Pos.TOP_LEFT

        val scrollPane = ScrollPane(tilePane)
        scrollPane.prefHeight = 500.0

        children.addAll(menuBar, scrollPane)
    }

    private fun updateImageList(menuItem: MenuItem) {
        tilePane.children.clear()
        menuItems[menuItem]?.let { menuTiles ->
            for (menuTile in menuTiles) {

                val imageView = menuTile.imageView
                imageView.fitWidth = TEXTURE_DISPLAY_SIZE
                imageView.fitHeight = TEXTURE_DISPLAY_SIZE

                val horizontalBox = HBox(imageView)
                horizontalBox.alignment = Pos.CENTER

                imageView.setOnMouseClicked {
                    selectedTile?.imageView?.style = ""
                    imageView.style = "-fx-effect: innershadow(gaussian, #039ed3, 2, 1.0, 0, 0);"
                    selectedTile = menuTile
                }

                tilePane.children.add(horizontalBox)
            }
        }
    }

    override fun onTileDraw(x: Int, y: Int): MenuTile? {
        return selectedTile
    }

    override fun getTileById(id: Int): MenuTile {
        menuItems.forEach { menu ->
            menu.value.forEach { menuTile ->
                if (menuTile.id == id) {
                    return menuTile
                }
            }
        }
        throw Exception("tile with id $id not found")
    }
}
