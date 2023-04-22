package com.flaviotps.mapeditor

import com.flaviotps.mapeditor.data.map.RawTile
import javafx.scene.image.ImageView


internal fun RawTile.toMenuTile(imageView: ImageView) = MenuTile(id, type, imageView)