package com.example.crossyroad_jimmy

import android.util.Log
import com.example.crossyroad_jimmy.model.GameModel
import java.util.*
import kotlin.concurrent.timer

class GamePresenter: GameContract.Presenter{
    //------------------------------------------------
    //상수 영역
    //

    //view, model 과 각각 연결되어 있음
    private lateinit var view: GameContract.View
    private lateinit var model: GameModel

    //모델과 뷰를 주기적으로 갱신하기 위한 타이머
    private lateinit var updateTimer: Timer

    override fun initSet(view: GameContract.View, displayWidth: Int, displayHeight: Int) {
        this.view = view
        this.model = GameModel(displayWidth, displayHeight)

        val frogPos = model.getFrogPosition()
        view.showFrogPosition(frogPos.first, frogPos.second)

        val snakes = model.getSnakes()
        view.showSnakes(snakes, model.snakeSize)

    }

    override fun frogJump() {
        model.frogJump()
        val frogPos = model.getFrogPosition()

        view.showFrogPosition(frogPos.first, frogPos.second)
        updateDataAboutLife()
    }

    override fun periodicUpdateTimerStart() {
        updateTimer = timer(period = model.updatePeriod){
            val frogPos = model.getFrogPosition()

            view.showFrogPosition(frogPos.first, frogPos.second)
            updateDataAboutNewFloatingObjects()
            updateDataAboutObjectsMoving()
            updateDataAboutObjectsRemoving()
            updateDataAboutLife()
        }
    }

    override fun periodicUpdateTimerStop() {
        updateTimer.cancel()
    }


    //------------------------------------------------
    //함수 영역 (주기적 갱신 시 실행되는 함수들)
    //

    private fun updateDataAboutNewFloatingObjects(){
        val newFloatingObjects = model.addFloatingObjects()

        view.showNewCrocodiles(newFloatingObjects.crocodiles, model.floatingObjectSize)
        view.showNewLogs(newFloatingObjects.logs, model.floatingObjectSize)
    }

    private fun updateDataAboutObjectsMoving(){
        model.moveFloatingObjects()

        view.showCrocodileMoving(model.getCrocodiles())
        view.showLogMoving(model.getLogs())
    }

    private fun updateDataAboutObjectsRemoving(){
        val removedFloatingObjects = model.removeFloatingObjects()

        view.hideRemovedCrocodiles(removedFloatingObjects.crocodiles)
        view.hideRemovedLogs(removedFloatingObjects.logs)
    }

    private fun updateDataAboutLife(){
        val life = model.getLife()
        val score = model.getScore()

        view.showLife(life)
        view.showScore(score)

        if(life <= 0)
            view.showGameEnd(score)
    }

}