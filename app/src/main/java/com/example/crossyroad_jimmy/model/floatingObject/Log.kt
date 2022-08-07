package com.example.crossyroad_jimmy.model.floatingObject

import android.util.Log
import com.example.crossyroad_jimmy.model.Frog
import com.example.crossyroad_jimmy.model.ObjectSize

class Log (id: Long, x: Float, y: Float, velocity: Float, size: ObjectSize)
    : FloatingObject(id, x, y, velocity, size) {
    private var endX = x + size.width
    private val endY = y + size.height

    override fun positionUpdate() {
        super.positionUpdate()
        endX += velocity
    }

    override fun isFrogFloating(frog: Frog): Boolean {
        Log.d("posss) fx, fex/ x, endx", "${frog.x}, ${frog.endX} / $x, $endX ")
        Log.d("posss) fy, fey/ y, endy", "${frog.y}, ${frog.endY} / $y, $endY ")

        if(frog.x < x || frog.endX > endX)
            return false

        if(!(frog.endY in y..endY))
            return false

        return true
    }
}