package com.example.crossyroad_jimmy.model.floatingObject

import com.example.crossyroad_jimmy.model.ObjectSize
import com.example.crossyroad_jimmy.model.Position

object CrocodileFactory: FloatingObjectFactory {
    override fun create(id: Long, position: Position, velocity: Float, size: ObjectSize): Crocodile {
        return Crocodile(id, position, velocity, size)
    }
}