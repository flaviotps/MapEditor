package com.flaviotps.mapeditor

import com.flaviotps.mapeditor.data.map.RawTile
import javafx.scene.image.Image
import javafx.scene.image.ImageView


internal fun RawTile.toMenuTile() = MenuTile(id, type, ImageView(Image("/image/${id}.png")))