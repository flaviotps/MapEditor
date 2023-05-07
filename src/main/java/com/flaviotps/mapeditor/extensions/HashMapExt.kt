package com.flaviotps.mapeditor.extensions

import java.lang.RuntimeException

fun <K, V : Any> HashMap<K, V>.getNonNull(key: K): V {
    this[key]?.let {
        return it
    } ?: run {
        throw RuntimeException("Value not found for key ${key.toString()}")
    }
}