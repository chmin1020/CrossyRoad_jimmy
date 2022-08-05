package com.example.crossyroad_jimmy.model

import kotlin.math.floor

object RandomNumber {
    fun get(start: Long, end: Long): Long{
        return floor(Math.random() * (end - start + 1)).toLong() + start
    }

    fun get(start: Int, end: Int): Int{
        return floor(Math.random() * (end - start + 1)).toInt() + start
    }

    fun get(start: Float, end: Float): Float{
        return (Math.random() * (end - start) + start).toFloat()
    }
}