import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Pane

class RightPanel : Pane() {

    init {
        this.style = "-fx-background-color: pink;"
        this.prefWidth = 400.0
        this.prefHeight = 400.0
    }

    fun getColumnConstraints(): ColumnConstraints {
        val col2 = ColumnConstraints()
        col2.percentWidth = 80.0
        return col2
    }
}
