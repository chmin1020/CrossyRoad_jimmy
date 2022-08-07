package com.example.crossyroad_jimmy.model.floatingObject

import com.example.crossyroad_jimmy.model.ObjectSize
import com.example.crossyroad_jimmy.model.Position

object LogFactory: FloatingObjectFactory {
    override fun create(id: Long, position: Position, velocity: Float, size: ObjectSize): Log {
        return Log(id, position, velocity, size)
    }
}