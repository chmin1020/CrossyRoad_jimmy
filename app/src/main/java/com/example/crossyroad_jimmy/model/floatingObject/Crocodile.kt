package com.example.crossyroad_jimmy.model.floatingObject

import com.example.crossyroad_jimmy.model.Frog
import com.example.crossyroad_jimmy.model.ObjectSize
import com.example.crossyroad_jimmy.model.Position

class Crocodile(id: Long, position: Position, velocity: Float, size: ObjectSize)
    : FloatingObject(id, position, velocity, size) {
    //악어는 등 범위를 파악하기 위한 상수 (주둥이의 길이, 등의 길이)
    private val snoutWidth = (size.width * 0.33F).toInt()
    private var backStartPosition = getPos()
    private var backEndPosition = Position(getPos().x + (size.width - snoutWidth), getPos().y + size.height)

    init {
        if(velocity < 0) {
            backStartPosition.x += snoutWidth
            backEndPosition.x += snoutWidth
        }
    }

    override fun positionUpdate() {
        super.positionUpdate()
        backStartPosition.x += velocity
        backEndPosition.x += velocity
    }

    override fun isFrogFloating(frog: Frog): Boolean {
        val frogPos = frog.getPos()
        val frogEndPos = Position(frogPos.x + frog.size.width, frogPos.y + frog.size.height)

        if(frogPos.x < backStartPosition.x || frogEndPos.x > backEndPosition.x)
            return false

        if(!(frogEndPos.y in getPos().y..backEndPosition.y))
            return false

        return true
    }
}