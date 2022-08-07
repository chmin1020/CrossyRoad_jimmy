package com.example.crossyroad_jimmy.model.floatingObject

import com.example.crossyroad_jimmy.model.Frog
import com.example.crossyroad_jimmy.model.ObjectSize
import com.example.crossyroad_jimmy.model.Position

class Log (id: Long, position: Position, velocity: Float, size: ObjectSize)
    : FloatingObject(id, position, velocity, size) {
    private var endPosition = Position(position.x + size.width, position.y + size.height)

    override fun positionUpdate() {
        super.positionUpdate()
        endPosition.x += velocity
    }

    override fun isFrogFloating(frog: Frog): Boolean {
        val frogPos = frog.getPos()
        val frogEndPos = Position(frogPos.x + frog.size.width, frogPos.y + frog.size.height)

        if(frogPos.x < getPos().x || frogEndPos.x > endPosition.x)
            return false

        if(!(frogEndPos.y in getPos().y..endPosition.y))
            return false

        return true
    }
}