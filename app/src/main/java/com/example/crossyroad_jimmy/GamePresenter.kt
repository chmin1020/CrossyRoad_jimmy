package com.example.crossyroad_jimmy

import com.example.crossyroad_jimmy.model.GameModel
import java.util.*
import kotlin.concurrent.timer

/** <Presenter 역할을 하는 클래스>
 *      Model, View 사이에서 둘 사이의 데이터 교환을 돕는다.
 *      따라서 모든 함수는 모델에서 갱신된 데이터를 받아서 뷰에 전달하는 단계로 나뉜다.
 *      크게 초기 설정, listener 응답(개구리 점프), timer 주기라는 3가지 상황에 내부 함수가 실행된다.
 **/
class GamePresenter: GameContract.Presenter{
    //------------------------------------------------
    //변수 영역
    //

    //view, model 과 각각 연결되어 있음
    private lateinit var gameView: GameContract.View
    private lateinit var gameModel: GameModel

    //모델과 뷰를 주기적으로 갱신하기 위한 타이머
    private lateinit var updateTimer: Timer


    //------------------------------------------------
    //함수 영역 (Presenter 역할을 위한 override)
    //

    /* 뷰와 모델을 세팅하고 게임 시작부터 설정해야 하는 값들을 적용하는 함수 */
    override fun gameInitSetting(view: GameContract.View, displayWidth: Int, displayHeight: Int) {
        gameView = view
        gameModel = GameModel(displayWidth, displayHeight)

        //게임 시작 시 개구리의 최초 위치와 무작위 뱀 설정 적용
        updateDataAboutFrogPosition()
        updateDataAboutSnakes()
        updateDataAboutScore()
    }

    /* 점프 버튼을 눌렀을 때, 개구리의 점프 관련 데이터의 흐름을 관리하는 함수 */
    override fun frogJump() {
        gameModel.frogJump() //개구리의 점프 실행 (위치 데이터 갱신)

        updateDataAboutFrogPosition() //갱신된 위치 데이터 적용
        updateDataAboutLife() //갱신된 목숨 적용
        updateDataAboutScore() //갱신된 점수 적용
    }

    /* 주기적으로 갱신되는 데이터의 흐름을 관리하기 위해 타이머를 실행하는 함수 */
    override fun periodicUpdateTimerStart() {
        updateTimer = timer(period = gameModel.updatePeriod){
            updateDataAboutFrogPosition() //개구리 위치 갱신
            updateDataAboutNewFloatingObjects() //악어와 통나무 추가
            updateDataAboutFloatingObjectsMoving() //악어와 통나무 위치 갱신
            updateDataAboutFloatingObjectsRemoving() //(화면 벗어난) 악어와 통나무 삭제
            updateDataAboutLife() //목숨 개수 및 죽음 여부 갱신
        }
    }

    /* 주기적 타이머를 종료하는 함수 */
    override fun periodicUpdateTimerStop() {
        updateTimer.cancel()
    }


    //------------------------------------------------
    // 내부 함수 영역
    //

    /* 뱀의 개수와 위치를 뷰에 적용하는 함수 (최초에만 실행) */
    private fun updateDataAboutSnakes(){
        val snakes = gameModel.getSnakes() //모델 불러오기
        gameView.showSnakes(snakes) //뷰에 전달
    }

    /* 개구리의 위치를 갱신하는 함수 */
    private fun updateDataAboutFrogPosition(){
        val frogPos = gameModel.getFrogPosition() //모델 불러오기
        gameView.showFrogPosition(frogPos.x, frogPos.y) //뷰에 전달
    }

    /* 떠다니는 물체(악어, 통나무)를 새로 화면에 추가하는 함수 */
    private fun updateDataAboutNewFloatingObjects(){
        //모델 갱신 및 불러오기
        val newFloatingObjects = gameModel.addFloatingObjects()

        //뷰에 전달
        gameView.showNewCrocodiles(newFloatingObjects.crocodiles)
        gameView.showNewLogs(newFloatingObjects.logs)
    }

    /* 화면에 있는 떠다니는 물체들을 움직이는 함수 */
    private fun updateDataAboutFloatingObjectsMoving(){
        //모델 갱신
        gameModel.moveFloatingObjects()

        //뷰에 전달
        gameView.showCrocodileMoving(gameModel.getCrocodiles())
        gameView.showLogMoving(gameModel.getLogs())
    }

    /* 화면을 벗어난 떠다니는 물체들을 지우는 함수 */
    private fun updateDataAboutFloatingObjectsRemoving(){
        //모델 갱신 및 불러오기
        val removedFloatingObjectIds = gameModel.removeFloatingObjects()

        //뷰에 전달
        gameView.hideRemovedCrocodiles(removedFloatingObjectIds.crocodileIds)
        gameView.hideRemovedLogs(removedFloatingObjectIds.logIds)
    }

    private fun updateDataAboutScore(){
        //모델 불러오기
        val score = gameModel.getScore()

        //뷰에 전달
        gameView.showScore(score)
    }

    /* 게임에서의 목숨과 점수 내용을 갱신하는 함수 */
    private fun updateDataAboutLife(){
        //모델 불러오기
        val life = gameModel.getLife()

        //뷰에 전달
        gameView.showLife(life)
        if(life <= 0)
            gameView.showGameEnd(gameModel.getScore())
    }
}