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
    //Presenter 역할을 위한 override 함수 영역
    //

    override fun initSet(view: GameContract.View, displayWidth: Int, displayHeight: Int) {
        this.view = view
        this.model = GameModel(displayWidth, displayHeight)

        updateDataAboutFrogPosition()
        updateDataAboutSnakes()
    }

    /* 점프 버튼을 눌렀을 때, 개구리의 점프 관련 데이터의 흐름을 관리하는 함수 */
    override fun frogJump() {
        model.frogJump() //개구리의 점프 실행 (위치 데이터 갱신)

        updateDataAboutFrogPosition() //갱신된 위치 데이터 적용
        updateDataAboutLife() //갱신된 목숨과 점수 적용
    }

    /* 주기적으로 갱신되는 데이터의 흐름을 관리하기 위해 타이머를 실행하는 함수 */
    override fun periodicUpdateTimerStart() {
        updateTimer = timer(period = model.updatePeriod){
            updateDataAboutFrogPosition() //개구리 위치 갱신
            updateDataAboutNewFloatingObjects() //악어와 통나무 추가
            updateDataAboutFloatingObjectsMoving() //악어와 통나무 위치 갱신
            updateDataAboutFloatingObjectsRemoving() //(화면 벗어난) 악어와 통나무 삭제
            updateDataAboutLife() //목숨 관련 데이터 갱신
        }
    }

    /* 주기적 타이머를 종료하는 함수 */
    override fun periodicUpdateTimerStop() {
        updateTimer.cancel()
    }


    //------------------------------------------------
    // 내부 함수 영역
    //

    /* 뱀의 개수와 위치를 뷰에 적용하는 함수 */
    private fun updateDataAboutSnakes(){
        val snakes = model.getSnakes()
        view.showSnakes(snakes, model.snakeSize)
    }

    /* 개구리의 위치를 갱신하는 함수 */
    private fun updateDataAboutFrogPosition(){
        val frogPos = model.getFrogPosition()
        view.showFrogPosition(frogPos.first, frogPos.second)
    }

    /* 떠다니는 물체(악어, 통나무)를 새로 화면에 추가하는 함수 */
    private fun updateDataAboutNewFloatingObjects(){
        val newFloatingObjects = model.addFloatingObjects()

        view.showNewCrocodiles(newFloatingObjects.crocodiles)
        view.showNewLogs(newFloatingObjects.logs)
    }

    /* 화면에 있는 떠다니는 물체들을 움직이는 함수 */
    private fun updateDataAboutFloatingObjectsMoving(){
        model.moveFloatingObjects()

        view.showCrocodileMoving(model.getCrocodiles())
        view.showLogMoving(model.getLogs())
    }

    /* 화면을 벗어난 떠다니는 물체들을 지우는 함수 */
    private fun updateDataAboutFloatingObjectsRemoving(){
        val removedFloatingObjects = model.removeFloatingObjects()

        view.hideRemovedCrocodiles(removedFloatingObjects.crocodiles)
        view.hideRemovedLogs(removedFloatingObjects.logs)
    }

    /* 게임에서의 목숨과 점수 내용을 갱신하는 함수 */
    private fun updateDataAboutLife(){
        val life = model.getLife()
        val score = model.getScore()

        view.showLife(life)
        view.showScore(score)

        if(life <= 0)
            view.showGameEnd(score)
    }
}