package com.example.crossyroad_jimmy.model.floatingObject

import com.example.crossyroad_jimmy.model.Frog
import com.example.crossyroad_jimmy.model.ObjectSize
import com.example.crossyroad_jimmy.model.Position

abstract class FloatingObject
    (val id: Long, pos: Position, val velocity: Float, val size: ObjectSize){
    private val position = pos.copy()

    fun getPos(): Position{
        return position.copy()
    }

    open fun positionUpdate(){
        position.x  += velocity
    }

    abstract fun isFrogFloating(frog: Frog): Boolean
}