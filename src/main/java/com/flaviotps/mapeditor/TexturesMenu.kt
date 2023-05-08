package com.flaviotps.mapeditor

import com.flaviotps.mapeditor.data.loader.ResourceLoader
import com.flaviotps.mapeditor.data.loader.imageCache
import com.flaviotps.mapeditor.state.Events
import com.flaviotps.mapeditor.state.MouseState
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.TilePane
import javafx.scene.layout.VBox
import org.koin.java.KoinJavaComponent.inject
import java.util.*


private const val TEXTURE_DISPLAY_SIZE = 32.0

class MenuTile(var id: Int, var type: String, var imageView: ImageView,val layer : Int? = null)

class TexturesMenu : VBox() {

    private var selectedTile: MenuTile? = null
    private val tilePane = TilePane()
    private val menu = Menu("Textures")
    private val menuBar = MenuBar()
    private val resourceLoader : ResourceLoader by inject(ResourceLoader::class.java)
    private var menuItems = mutableMapOf<MenuItem, List<MenuTile>>()
    private val events : Events by inject(Events::class.java)

    init {
        style = "-fx-background-color: grey;"
        prefWidth = 5 * TEXTURE_DISPLAY_SIZE
        prefHeight = SCENE_HEIGHT * 0.7

        resourceLoader.loadTilesMenu().forEachIndexed { index, tileSet ->
            val menuItem = MenuItem(tileSet.name)
            val tiles = tileSet.raw.map { rawTile ->
                val url = "/image/${tileSet.name.lowercase(Locale.getDefault())}/${rawTile.id}.png"
                val image = Image(url)
                imageCache[rawTile.id] = image
                val imageView = ImageView(image)
                rawTile.toMenuTile(imageView)
            }
            menuItems[menuItem] = tiles
            menu.items.add(menuItem)
            menuItem.setOnAction {
                updateImageList(menuItem)
            }
            if (index == 0) {
                updateImageList(menuItem)
            }
        }

        menuBar.menus.add(menu)
        tilePane.padding = Insets(1.0)
        tilePane.hgap = 1.0
        tilePane.vgap = 1.0
        tilePane.alignment = Pos.TOP_LEFT
        val scrollPane = ScrollPane(tilePane)
        scrollPane.prefHeight = prefHeight
        children.addAll(menuBar, scrollPane)
    }


    private fun updateImageList(menuItem: MenuItem) {
        tilePane.children.clear()
        menuItems[menuItem]?.let { menuTiles ->
            for (menuTile in menuTiles) {
                val imageView = menuTile.imageView
                imageView.fitWidth = TEXTURE_DISPLAY_SIZE
                imageView.fitHeight = TEXTURE_DISPLAY_SIZE
                imageView.setOnMouseClicked {
                    events.mouseState = MouseState.TextureSelected(menuTile)
                    selectedTile?.imageView?.style = ""
                    imageView.style = "-fx-effect: innershadow(gaussian, #039ed3, 2, 1.0, 0, 0);"
                    selectedTile = menuTile
                }
                tilePane.children.add(imageView)
            }
        }
    }
}
