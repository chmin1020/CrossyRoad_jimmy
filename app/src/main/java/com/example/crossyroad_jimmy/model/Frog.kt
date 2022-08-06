package com.example.crossyroad_jimmy.model

class Frog(speed: Float, private val startX: Float, private val startY: Float) {
    private val frogSpeed = speed

    var x = startX
        private set
    var y = startY
        private set

    var life = 3
        private set
    var score = 0
        private set

    var isFloating = false
        private set
    var floatingVelocity = 0F
        private set


    fun positionReset(){
        x = startX
        y = startY
    }

    fun frontJump(){
        y -= frogSpeed
    }

    fun floatingSetting(velocity: Float){
        isFloating = true
        floatingVelocity = velocity
    }

    fun floatingReset(){
        isFloating = false
    }

    fun frogFloating(){
        x += floatingVelocity
    }

    fun frogDead(){
        life -= 1
    }

    fun scoreIncrease(){
        score += 1
    }
}