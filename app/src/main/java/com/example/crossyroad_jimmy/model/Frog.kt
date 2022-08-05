package com.example.crossyroad_jimmy.model

class Frog(speed: Float, private val startX: Float, private val startY: Float) {
    private val frogSpeed = speed

    var currentX = startX
        private set
    var currentY = startY
        private set

    fun positionReset(){
        currentX = startX
        currentY = startY
    }

    fun frontJump(){
        currentY -= frogSpeed
    }

    fun floating(){

    }
}