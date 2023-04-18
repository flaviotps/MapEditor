import com.flaviotps.mapeditor.*
import com.flaviotps.mapeditor.data.loader.ResourceLoader
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


class LeftPanel : VBox() {

    private var selectedImageView: ImageView? = null
    private val tilePane = TilePane()
    private val menu = Menu("Textures")
    private val menuBar = MenuBar()
    private val resourceLoader = ResourceLoader()
    private var menuItems = mutableMapOf<MenuItem, List<Image>>()

    fun selectedImage() = selectedImageView?.image

    init {
        style = "-fx-background-color: grey;"
        prefWidth = 0.2 * 600 // Blue column occupies 10% of the screen

        resourceLoader.loadTiles().forEach {
            val menuItem = MenuItem(it.name)
            val tiles = it.raw.map { rawTile -> Image("/image/${rawTile.id}.png") }
            menuItems[menuItem] = tiles
            menu.items.add(menuItem)
            menuItem.setOnAction { updateImageList(menuItem) }
        }

        // Add the menu to the menu bar
        menuBar.menus.add(menu)

        // Create the image list
        tilePane.padding = Insets(1.0)
        tilePane.hgap = 1.0
        tilePane.vgap = 1.0
        tilePane.alignment = Pos.TOP_LEFT

        // Wrap the image list in a scroll pane
        val scrollPane = ScrollPane(tilePane)
        scrollPane.prefHeight = 500.0

        // Add the menu bar and image list to the pane
        children.addAll(menuBar, scrollPane)
    }

    private fun updateImageList(menuItem: MenuItem) {
        tilePane.children.clear()
        menuItems[menuItem]?.let { imageList ->
            for (image in imageList) {
                val imageView = ImageView(image)
                imageView.fitWidth = TEXTURE_DISPLAY_SIZE
                imageView.fitHeight = TEXTURE_DISPLAY_SIZE

                val horizontalBox = HBox(imageView)
                horizontalBox.alignment = Pos.CENTER

                imageView.setOnMouseClicked {
                    selectedImageView?.style = ""
                    selectedImageView = imageView
                    selectedImageView?.style = "-fx-effect: innershadow(gaussian, #039ed3, 2, 1.0, 0, 0);"
                }
                tilePane.children.add(horizontalBox)
            }
        }
    }
}
