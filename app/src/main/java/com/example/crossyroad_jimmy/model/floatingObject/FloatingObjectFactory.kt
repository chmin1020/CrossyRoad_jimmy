package com.example.crossyroad_jimmy.model.floatingObject

import com.example.crossyroad_jimmy.model.ObjectSize
import com.example.crossyroad_jimmy.model.Position

interface FloatingObjectFactory {
    fun create(id: Long, position: Position, velocity: Float, size: ObjectSize): FloatingObject
}