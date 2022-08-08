package com.example.crossyroad_jimmy.model

/**
 * 게임의 메인 캐릭터가 되는 개구리를 담당하는 클래스.
 * 따라서 life, score 등의 게임 속 변수들 역시 가지고 있음.
 * 대부분의 변수와 함수들은 모두 이동(점프, 떠다님)과 관련되어 있다.
 */
class Frog(distance: Float, private val defaultPosition: Position, val size: ObjectSize) {
    // 게임의 진행과 관련있는 변수들 (목숨, 점수)
    var life = 3
        private set
    var score = 0
        private set

    // Floating 상태(FloatingObject 탑승) 판별과 이때의 속도를 위한 변수
    var isFloating = false
        private set
    var floatingVelocity = 0F
        private set

    //개구리의 현재 위치
    private val position = defaultPosition.copy()

    //개구리가 점프할 때 뛰는 거리
    private val jumpDistance = distance

    /* 개구리가 점프하는 함수 (-y 방향으로 이동) */
    fun jump(){
        position.y -= jumpDistance
    }

    /* 개구리의 죽음을 처리하는 함수 */
    fun frogDead(){
        positionReset()
        floatingReset()
        life -= 1
    }

    /* 개구리의 현재 위치를 알리는 함수 */
    fun getPos(): Position{
        return position
    }

    /* 위치 초기화를 하는 함수 */
    fun positionReset(){
        position.x = defaultPosition.x
        position.y = defaultPosition.y
    }

    /* 개구리가 floating 상태가 되었을 때 관련 변수 세팅을 하는 함수 */
    fun floatingSetting(velocity: Float){
        isFloating = true
        floatingVelocity = velocity
    }

    /* floating 상태 초기화를 담당하는 함수 */
    fun floatingReset(){
        isFloating = false
    }

    /* floating 상태에서 움직이는 부분을 담당하는 함수 */
    fun floatingFrogMove(){
        position.x += floatingVelocity
    }

    /* 점수를 1 증가시키는 함수 */
    fun scoreIncrease(){
        score += 1
    }
}