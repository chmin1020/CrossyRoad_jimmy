package com.example.crossyroad_jimmy.model

class Frog(distance: Float, private val startX: Float, private val startY: Float, val size: ObjectSize) {
    var x = startX
        private set
    var y = startY
        private set

    var endX = x + size.width
        private set
    var endY = y + size.height
        private set

    var life = 3
        private set
    var score = 0
        private set

    var isFloating = false
        private set
    var floatingVelocity = 0F
        private set

    private val jumpDistance = distance

    fun positionReset(){
        x = startX
        y = startY
        endX = x + size.width
        endY = y + size.height
    }

    fun jump(){
        y -= jumpDistance
        endY -= jumpDistance
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
        endX += floatingVelocity
    }

    fun frogDead(){
        positionReset()
        floatingReset()
        life -= 1
    }

    fun scoreIncrease(){
        score += 1
    }
}