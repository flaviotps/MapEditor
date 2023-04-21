package com.flaviotps.mapeditor.map

import com.flaviotps.mapeditor.MenuTile

interface MapCallbacks {
    fun onTileDraw(x : Int, y : Int) : MenuTile?
    fun getTileById(id : Int) : MenuTile
}