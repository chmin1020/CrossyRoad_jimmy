package com.example.crossyroad_jimmy.model

import kotlin.math.floor

/**
 * 난수를 범위에 맞게 생성하기 위한 randomNumber Factory.
 * 자료형 타입(Long, Int, Float)에 따라 get()이 오버로딩됨.
 */
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