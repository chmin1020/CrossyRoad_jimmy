package com.example.crossyroad_jimmy.model.floatingObject

import android.util.Log
import com.example.crossyroad_jimmy.model.Frog
import com.example.crossyroad_jimmy.model.ObjectSize

abstract class FloatingObject
    (val id: Long, x: Float, val y: Float, val velocity: Float, val size: ObjectSize){
    var x = x
        private set

    open fun positionUpdate(){
        x += velocity
    }

    abstract fun isFrogFloating(frog: Frog): Boolean
}