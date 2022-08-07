package com.example.crossyroad_jimmy

import com.example.crossyroad_jimmy.model.Snake
import com.example.crossyroad_jimmy.model.floatingObject.FloatingObject

interface GameContract {
    interface Presenter{
        //초기 설정
        fun initSet(view: View, displayWidth: Int, displayHeight: Int)

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
        fun showNewCrocodiles(crocodiles: List<FloatingObject>)
        fun showNewLogs(logs: List<FloatingObject>)
        fun showCrocodileMoving(crocodiles: List<FloatingObject>)
        fun showLogMoving(logs: List<FloatingObject>)
        fun hideRemovedCrocodiles(crocodiles: List<FloatingObject>)
        fun hideRemovedLogs(logs: List<FloatingObject>)

        //개구리의 움직임에 따른 변경
        fun showScore(score: Int)
        fun showLife(life: Int)
        fun showGameEnd(score: Int)
    }
}