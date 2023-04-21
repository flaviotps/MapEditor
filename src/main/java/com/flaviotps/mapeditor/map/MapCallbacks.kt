package com.flaviotps.mapeditor.map

import com.flaviotps.mapeditor.MenuTile

interface MapCallbacks {
    fun onTileDraw() : MenuTile?
    fun getTileById(id : Int) : MenuTile
}