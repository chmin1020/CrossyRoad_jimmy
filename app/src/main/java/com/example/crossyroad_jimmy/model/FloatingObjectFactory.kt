package com.example.crossyroad_jimmy.model

import com.example.crossyroad_jimmy.model.floatingObject.Crocodile
import com.example.crossyroad_jimmy.model.floatingObject.FloatingObject
import com.example.crossyroad_jimmy.model.floatingObject.Log

object FloatingObjectFactory {
    const val CROCODILE = 0
    const val LOG = 1

    fun create(type: Int, id: Long, firstX: Float, firstY: Float, velocity: Float): FloatingObject{
        if(type == CROCODILE)
            return Crocodile(id, firstX, firstY, velocity)
        return Log(id, firstX, firstY, velocity)
    }
}