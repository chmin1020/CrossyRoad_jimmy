package com.example.crossyroad_jimmy.model.floatingObject

import com.example.crossyroad_jimmy.model.Frog
import com.example.crossyroad_jimmy.model.ObjectSize
import com.example.crossyroad_jimmy.model.Position

/**
 * 게임 속 떠다니는 객체를 담당하는 (추상)클래스.
 * 특징 상 위치, 속도, 크기에 해당하는 프로퍼티를 가진다.
 * 다른 떠다니는 객체들의 세부 종류들이 이 클래스를 상속받는다.
 */
abstract class FloatingObject(pos: Position, val velocity: Float, val size: ObjectSize){
    //떠다니는 물체의 현 위치
    private val position = pos.copy()

    /* 현 위치 반환, 외부에서 position 변경하는 것을 막기 위함 */
    fun getPos(): Position{
        return position.copy()
    }

    /* 위치 갱신 함수, 기본적으로 x값이 달라짐 */
    open fun positionUpdate(){
        position.x  += velocity
    }

    /* 개구리의 탑승 구분 여부는 subclass 에 따라 다르게 처리 */
    abstract fun isFrogFloating(frog: Frog): Boolean
}