package com.example.crossyroad_jimmy.model.floatingObject

abstract class FloatingObject(val id: Long, firstX: Float, firstY: Float, val velocity: Float){
    var x = firstX
        private set
    val y = firstY

    fun positionUpdate(){
        x += velocity
    }
}