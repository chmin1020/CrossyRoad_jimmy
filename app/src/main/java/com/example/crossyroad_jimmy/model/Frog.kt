package com.example.crossyroad_jimmy.model

class Frog(distance: Float, private val defaultPosition: Position, val size: ObjectSize) {
    private val position = defaultPosition.copy()

    var life = 3
        private set
    var score = 0
        private set

    var isFloating = false
        private set
    var floatingVelocity = 0F
        private set

    private val jumpDistance = distance

    fun getPos(): Position{
        return position
    }

    fun positionReset(){
        position.x = defaultPosition.x
        position.y = defaultPosition.y
    }

    fun jump(){
        position.y -= jumpDistance
    }

    fun floatingSetting(velocity: Float){
        isFloating = true
        floatingVelocity = velocity
    }

    fun floatingReset(){
        isFloating = false
    }

    fun frogFloating(){
        position.x += floatingVelocity
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