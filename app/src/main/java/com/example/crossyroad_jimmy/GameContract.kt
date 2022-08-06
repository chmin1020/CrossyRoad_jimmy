package com.example.crossyroad_jimmy

import com.example.crossyroad_jimmy.model.ObjectSize
import com.example.crossyroad_jimmy.model.Snake
import com.example.crossyroad_jimmy.model.floatingObject.Crocodile
import com.example.crossyroad_jimmy.model.floatingObject.Log

interface GameContract {
    interface Presenter{
        //초기 설정
        fun initSet(view: GameContract.View, displayWidth: Int, displayHeight: Int)

        //사용자 입력
        fun frogJump()

        //주기적인 데이터 업데이트 과정에서 호출
        fun periodicUpdateTimerStart()
        fun periodicUpdateTimerStop()
    }

    interface View{
        //초기 설정
        fun showSnakes(snakes: List<Snake>, size: ObjectSize)

        //사용자 입력
        fun showFrogPosition(x: Float, y: Float)

        //주기적인 데이터 업데이트 과정에서 호출
        fun showNewCrocodiles(crocodiles: List<Crocodile>, size: ObjectSize)
        fun showNewLogs(logs: List<Log>, size: ObjectSize)
        fun showCrocodileMoving(crocodiles: List<Crocodile>)
        fun showLogMoving(logs: List<Log>)
        fun hideRemovedCrocodiles(crocodiles: List<Crocodile>)
        fun hideRemovedLogs(logs: List<Log>)

        //개구리의 움직임에 따른 변경
        fun showScore(score: Int)
        fun showLife(life: Int)
        fun showGameEnd(score: Int)
    }
}