package com.example.crossyroad_jimmy.model.floatingObject

import com.example.crossyroad_jimmy.model.ObjectSize

object FloatingObjectFactory {
    const val CROCODILE = 0
    const val LOG = 1

    fun create(type: Int, id: Long, x: Float, y: Float, velocity: Float, size: ObjectSize): FloatingObject{
        if(type == CROCODILE)
            return Crocodile(id, x, y, velocity, size)
        return Log(id, x, y, velocity, size)
    }
}