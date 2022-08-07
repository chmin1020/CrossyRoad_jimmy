package com.example.crossyroad_jimmy.model.floatingObject

import com.example.crossyroad_jimmy.model.Frog
import com.example.crossyroad_jimmy.model.ObjectSize

class Crocodile(id: Long, x: Float, y: Float, velocity: Float, size: ObjectSize)
    : FloatingObject(id, x, y, velocity, size) {
    //악어는 등 범위를 파악하기 위한 상수 (주둥이의 길이, 등의 길이)
    private val snoutWidth = (size.width * 0.33F).toInt()
    private var backStartX = x
    private var backEndX = x + (size.width - snoutWidth)
    private val backEndY = y + size.height

    init {
        if(velocity < 0) {
            backStartX += snoutWidth
            backEndX += snoutWidth
        }
    }

    override fun positionUpdate() {
        super.positionUpdate()
        backStartX += velocity
        backEndX += velocity
    }

    override fun isFrogFloating(frog: Frog): Boolean {
        if(frog.x < backStartX || frog.endX > backEndX)
            return false

        if(!(frog.endY in y..backEndY))
            return false

        return true
    }
}