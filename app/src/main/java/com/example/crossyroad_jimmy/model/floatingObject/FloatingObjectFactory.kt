package com.example.crossyroad_jimmy.model.floatingObject

import com.example.crossyroad_jimmy.model.ObjectSize
import com.example.crossyroad_jimmy.model.Position

object FloatingObjectFactory {
    const val CROCODILE = 0
    const val LOG = 1

    fun create(type: Int, id: Long, position: Position, velocity: Float, size: ObjectSize): FloatingObject{
        if(type == CROCODILE)
            return Crocodile(id, position, velocity, size)
        return Log(id, position, velocity, size)
    }
}