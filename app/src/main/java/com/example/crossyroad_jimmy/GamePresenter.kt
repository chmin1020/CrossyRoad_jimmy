package com.example.crossyroad_jimmy

import com.example.crossyroad_jimmy.model.GameModel
import java.util.*
import kotlin.concurrent.timer

class GamePresenter: GameContract.Presenter{
    //------------------------------------------------
    //??? 영역
    //

    //view, model 과 각각 연결되어 있음
    private lateinit var view: GameContract.View
    private lateinit var model: GameModel

    //모델과 뷰를 주기적으로 갱신하기 위한 타이머
    private lateinit var updateTimer: Timer


    //------------------------------------------------
    //??? 영역
    //

    override fun initSet(view: GameContract.View, displayWidth: Int, displayHeight: Int) {
        this.view = view
        this.model = GameModel(displayWidth, displayHeight)

        updateDataAboutFrogPosition()
        updateDataAboutSnakes()
    }

    override fun frogJump() {
        model.frogJump() //개구리의 점프 실행 (위치 데이터 갱신)

        updateDataAboutFrogPosition() //갱신된 위치 데이터 적용
        updateDataAboutLife() //갱신된 목숨과 점수 적용
    }

    override fun periodicUpdateTimerStart() {
        updateTimer = timer(period = model.updatePeriod){
            updateDataAboutFrogPosition()
            updateDataAboutNewFloatingObjects()
            updateDataAboutFloatingObjectsMoving()
            updateDataAboutFloatingObjectsRemoving()
            updateDataAboutLife()
        }
    }

    override fun periodicUpdateTimerStop() {
        updateTimer.cancel()
    }


    //------------------------------------------------
    // 내부 함수 영역
    //

    private fun updateDataAboutFrogPosition(){
        val frogPos = model.getFrogPosition()
        view.showFrogPosition(frogPos.first, frogPos.second)
    }

    private fun updateDataAboutSnakes(){
        val snakes = model.getSnakes()
        view.showSnakes(snakes, model.snakeSize)
    }

    private fun updateDataAboutNewFloatingObjects(){
        val newFloatingObjects = model.addFloatingObjects()

        view.showNewCrocodiles(newFloatingObjects.crocodiles, model.floatingObjectSize)
        view.showNewLogs(newFloatingObjects.logs, model.floatingObjectSize)
    }

    private fun updateDataAboutFloatingObjectsMoving(){
        model.moveFloatingObjects()

        view.showCrocodileMoving(model.getCrocodiles())
        view.showLogMoving(model.getLogs())
    }

    private fun updateDataAboutFloatingObjectsRemoving(){
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