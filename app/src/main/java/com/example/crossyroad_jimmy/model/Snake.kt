package com.example.crossyroad_jimmy.model

/**
 * 뱀 객체를 나타내는 클래스.
 * 한 번 생성된 뒤 변화가 없으므로 내용이 많지 않음.
 */
class Snake(private val position: Position, val size: ObjectSize){
    fun getPos(): Position{
        return position
    }
}