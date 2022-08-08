package com.example.crossyroad_jimmy

import com.example.crossyroad_jimmy.model.Snake
import com.example.crossyroad_jimmy.model.floatingObject.Crocodile
import com.example.crossyroad_jimmy.model.floatingObject.Log

/**
 * MVP 패턴에서 View - Presenter 의 관계를 묘사하고 각각의 역할을 정의한 인터페이스
 * 내부 View, Presenter 인터페이스는 각 요소를 담당하는 클래스가 구현한다.
 */
interface GameContract {
    interface Presenter{
        //초기 설정
        fun gameInitSetting(view: View, displayWidth: Int, displayHeight: Int)

        //사용자 입력
        fun frogJump()

        //주기적인 데이터 업데이트 과정에서 호출
        fun periodicUpdateTimerStart()
        fun periodicUpdateTimerStop()
    }

    interface View{
        //초기 설정
        fun showSnakes(snakes: List<Snake>)

        //사용자 입력
        fun showFrogPosition(x: Float, y: Float)

        //주기적인 데이터 업데이트 과정에서 호출
        fun showNewCrocodiles(crocodiles: Map<Long, Crocodile>)
        fun showNewLogs(logs: Map<Long, Log>)
        fun showCrocodileMoving(crocodiles: Map<Long, Crocodile>)
        fun showLogMoving(logs: Map<Long, Log>)
        fun hideRemovedCrocodiles(crocodiles: List<Long>)
        fun hideRemovedLogs(logs: List<Long>)

        //개구리의 움직임에 따른 변경
        fun showScore(score: Int)
        fun showLife(life: Int)
        fun showGameEnd(score: Int)
    }
}