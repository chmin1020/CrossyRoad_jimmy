package com.example.crossyroad_jimmy.model.floatingObject

import com.example.crossyroad_jimmy.model.Frog
import com.example.crossyroad_jimmy.model.ObjectSize
import com.example.crossyroad_jimmy.model.Position

/**
 * 게임 속 떠다니는 객체, 그 중에서 악어를 담당하는 클래스.
 * FloatingObject 클래스와 많은 특징을 공유하나, 개구리의 탑승 범위가 다름 (주둥이 제외)
 */
class Crocodile(position: Position, velocity: Float, size: ObjectSize)
    : FloatingObject(position, velocity, size) {
    //악어는 등 범위를 파악하기 위한 상수 (주둥이의 길이)
    private val snoutWidth = (size.width * 0.33F).toInt()

    //악어 등의 범위
    private var backStartPosition = getPos()
    private var backEndPosition = Position(getPos().x + (size.width - snoutWidth), getPos().y + size.height)

    /* 생성자 - 악어의 방향에 따라 등의 시작 위치를 다르게 적용 */
    init {
        if(velocity < 0) {
            backStartPosition.x += snoutWidth
            backEndPosition.x += snoutWidth
        }
    }

    /* 기존 위치 갱신에 더하여 악어의 등 범위를 표시하는 position 들도 갱신 */
    override fun positionUpdate() {
        super.positionUpdate()
        backStartPosition.x += velocity
        backEndPosition.x += velocity
    }

    /* 개구리가 탑승했는지 확인하는 함수, 악어는 등 범위를 통해서 파악 */
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