package com.example.crossyroad_jimmy.model.floatingObject

import com.example.crossyroad_jimmy.model.Frog
import com.example.crossyroad_jimmy.model.ObjectSize
import com.example.crossyroad_jimmy.model.Position

/**
 * 게임 속 떠다니는 객체, 그 중에서 통나무를 담당하는 클래스.
 * FloatingObject 클래스와 많은 특징을 공유하고,
 * 개구리 탑승 여부를 쉽게 확인할 수 있도록 endPosition 변수를 가짐.
 */
class Log (position: Position, velocity: Float, size: ObjectSize)
    : FloatingObject(position, velocity, size) {
    //통나무의 끝부분을 표시하는 position
    private var endPosition = Position(position.x + size.width, position.y + size.height)

    /* 기존 위치 갱신에 더하여 통나무의 끝을 표시하는 position 도 갱신 */
    override fun positionUpdate() {
        super.positionUpdate()
        endPosition.x += velocity
    }

    /* 개구리가 탑승했는지 확인하는 함수, 통나무는 전체 범위로 파악 */
    override fun isFrogFloating(frog: Frog): Boolean {
        val frogPos = frog.getPos()
        val frogEndPos = Position(frogPos.x + frog.size.width, frogPos.y + frog.size.height)

        if(frogPos.x < getPos().x || frogEndPos.x > endPosition.x)
            return false

        if(frogEndPos.y !in getPos().y..endPosition.y)
            return false

        return true
    }
}