package com.flaviotps.mapeditor.di


import com.flaviotps.mapeditor.data.loader.ResourceLoader
import com.flaviotps.mapeditor.state.Events
import com.flaviotps.mapeditor.state.MouseState
import org.koin.dsl.module

var koinModules = module {
    single { Events(MouseState.None) }
    single { ResourceLoader() }
    single { get<Events>().mouseState }
}