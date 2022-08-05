package com.example.crossyroad_jimmy.model

class River(val entranceX: Float, val entranceY: Float, val direction: Int, private val timePeriod: Long) {
    var currentTime: Long = 0L


    fun riverTimeIncrease(time: Long){
        currentTime += time
    }

    fun isItTimeForNewFloatingObject(): Boolean{
        if(currentTime < timePeriod)
            return false

        currentTime = 0L
        return true
    }
}