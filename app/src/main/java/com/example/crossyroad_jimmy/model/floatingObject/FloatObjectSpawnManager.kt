package com.example.crossyroad_jimmy.model.floatingObject

import com.example.crossyroad_jimmy.model.ObjectSize
import com.example.crossyroad_jimmy.model.Position

/**
 * 악어와 통나무의 생성을 담당하는 객체
 * 기능적으로는 FloatingObject 객체가 생성될 시간, 위치를 지정하기 위한 클래스
 * 내부 메소드로 본인의 수동 타이머를 돌리고 초기화하는 것을 반복한다.
 */
class FloatObjectSpawnManager(private val spawnPosition: Position, direction: Int, private val timePeriod: Long) {
    //내부 타이머 현재 시간
    var currentTime: Long = 0L

    //이 매니저에 의해 생성될 객체들의 속력 & 속도
    private val floatingSpeed = 25F
    private val floatingVelocity = direction * floatingSpeed


    /* time 값 만큼 타이머 시간을 늘리는 함수 */
    fun currentTimeIncrease(time: Long){
        currentTime += time
    }

    /* spawn 시간이 다 되었음을 알려주는 함수 */
    fun isItTimeForNewFloatingObject(): Boolean{
        if(currentTime < timePeriod)
            return false
        return true
    }

    /* spawn 시간 타이머를 초기화하는 함수 */
    fun resetCurrentTime(){
        currentTime = 0L
    }

    /* 악어를 생성하여 제공하는 함수 */
    fun spawnCrocodile(floatingObjectSize: ObjectSize): Crocodile{
        return Crocodile(spawnPosition, floatingVelocity, floatingObjectSize)
    }

    /* 통나무를 생성하여 제공하는 함수 */
    fun spawnLog(floatingObjectSize: ObjectSize): Log{
        return Log(spawnPosition, floatingVelocity, floatingObjectSize)
    }
}