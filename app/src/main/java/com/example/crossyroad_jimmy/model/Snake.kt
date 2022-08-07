package com.example.crossyroad_jimmy.model

class Snake(private val position: Position, val size: ObjectSize){
    fun getPos(): Position{
        return position
    }
}