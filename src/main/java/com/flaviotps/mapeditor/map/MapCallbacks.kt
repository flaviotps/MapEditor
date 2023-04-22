package com.flaviotps.mapeditor.map

import com.flaviotps.mapeditor.MenuTile

interface MapCallbacks {
    fun getTileById(id : Int) : MenuTile
}