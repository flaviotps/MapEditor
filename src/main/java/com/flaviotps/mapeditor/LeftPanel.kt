import com.flaviotps.mapeditor.*
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
    private val menu = Menu("Tiles")
    private val menuBar = MenuBar()
    private var menuItems = mapOf<MenuItem, List<Image>>()

    fun selectedImage(): Image? {
        return selectedImageView?.image
    }

    init {
        style = "-fx-background-color: blue;"
        prefWidth = 0.2 * 600 // Blue column occupies 10% of the screen

        // Create the menu items
        val groundMenuItem = MenuItem("Ground")
        val wallMenuItem = MenuItem("Wall")
        val natureMenuItem = MenuItem("Nature")

        // Add the menu items to the menu
        menu.items.addAll(groundMenuItem, wallMenuItem, natureMenuItem)

        // Add the menu to the menu bar
        menuBar.menus.add(menu)

        // Create the image list

        tilePane.padding = Insets(1.0)
        tilePane.hgap = 1.0
        tilePane.vgap = 1.0
        tilePane.alignment = Pos.TOP_LEFT

        // Add click listeners to the menu items to update the image list
        groundMenuItem.setOnAction { updateImageList(groundMenuItem) }
        wallMenuItem.setOnAction { updateImageList(wallMenuItem) }
        natureMenuItem.setOnAction { updateImageList(natureMenuItem) }

        // Add the images to the list
        // Add the menu items and their corresponding image lists
        menuItems = mapOf(
            groundMenuItem to listOf(
                Image(GRASS_101),
                Image(GRASS_102),
                Image(GRASS_103),
                Image(GRASS_104)
            ),
            wallMenuItem to listOf(
                Image(WALL_600),
                Image(WALL_601),
                Image(WALL_602)
            ),
            natureMenuItem to listOf(
                Image(ITEM_1113)
            )
        )

        updateImageList(groundMenuItem)

        // Wrap the image list in a scroll pane
        val scrollPane = ScrollPane(tilePane)
        scrollPane.prefHeight = 500.0

        // Add the menu bar and image list to the pane
        children.addAll(menuBar, scrollPane)
    }

    private fun updateImageList(menuItem: MenuItem) {
        tilePane.children.clear()
        val imageList = menuItems[menuItem]
        if (imageList != null) {
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
