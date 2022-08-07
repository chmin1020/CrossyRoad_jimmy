package com.example.crossyroad_jimmy

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.viewbinding.ViewBinding
import com.example.crossyroad_jimmy.databinding.ActivityGameBinding
import com.example.crossyroad_jimmy.model.ObjectSize
import com.example.crossyroad_jimmy.model.Position
import com.example.crossyroad_jimmy.model.Snake
import com.example.crossyroad_jimmy.model.floatingObject.Crocodile
import com.example.crossyroad_jimmy.model.floatingObject.FloatingObject
import com.example.crossyroad_jimmy.model.floatingObject.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class GameView(private val context: Context, private val presenter: GameContract.Presenter): GameContract.View {
    //ui 갱신을 위해 사용할 CoroutineScope
    private val uiThread = MainScope()

    //탄환과 적 객체 대응 뷰들을 관리하는 테이블
    private val crocodileTable = mutableMapOf<Long,ImageView>()
    private val logTable = mutableMapOf<Long,ImageView>()

    //뷰 바인딩 객체
    private lateinit var binder: ActivityGameBinding


    //------------------------------------------------
    // 함수 영역 (뷰 설정)
    //

    /* 표시할 뷰와 이벤트를 세팅하고 vewBinding 객체를 반환하는 함수 */
    fun initSet(): ViewBinding {
        binder = ActivityGameBinding.inflate((context as Activity).layoutInflater)
        initListener()
        return binder
    }

    /* 뷰 이벤트 세팅 함수 */
    private fun initListener(){
        //점프 버튼
        binder.btnMove.setOnClickListener {
            presenter.frogJump()
        }
    }


    //------------------------------------------------
    //함수 영역 (GameContract.View 오버라이딩)
    //

    /* 개구리를 위치에 맞게 그리는 함수 */
    override fun showFrogPosition(x: Float, y: Float) {
        uiThread.launch {
            binder.frog.x = x
            binder.frog.y = y
        }
    }

    /* 무작위 설정된 뱀들을 그리는 함수 */
    override fun showSnakes(snakes: List<Snake>) {
        val snakeIterator = snakes.iterator()

        while(snakeIterator.hasNext()){
            makeNewSnakeView(snakeIterator.next())
        }
    }

    /* 새로운 악어들을 그리는 함수 */
    override fun showNewCrocodiles(crocodiles: List<FloatingObject>) {
        uiThread.launch {
            val crocodileIterator = crocodiles.iterator()
            var eachCrocodile: FloatingObject

            while (crocodileIterator.hasNext()) {
                eachCrocodile = crocodileIterator.next()
                crocodileTable[eachCrocodile.id] = makeNewCrocodileView(eachCrocodile)
            }
        }
    }

    /* 새로운 통나무들을 그리는 함수 */
    override fun showNewLogs(logs: List<FloatingObject>) {
        uiThread.launch {
            val logIterator = logs.iterator()
            var eachLog: FloatingObject

            while (logIterator.hasNext()) {
                eachLog = logIterator.next()
                logTable[eachLog.id] = makeNewLogView(eachLog)
            }
        }
    }

    /* 악어를 위치에 맞게 이동시켜서 그리는 함수 */
    override fun showCrocodileMoving(crocodiles: List<FloatingObject>) {
        uiThread.launch {
            eachViewMovingUpdate(crocodiles, crocodileTable)
        }
    }

    /* 통나무를 위치에 맞게 이동시켜서 그리는 함수 */
    override fun showLogMoving(logs: List<FloatingObject>) {
        uiThread.launch {
            eachViewMovingUpdate(logs, logTable)
        }
    }

    /* 화면을 벗어난 악어들을 지우는 함수 */
    override fun hideRemovedCrocodiles(crocodiles: List<FloatingObject>) {
        uiThread.launch {
            eachViewRemovingUpdate(crocodiles, crocodileTable)
        }
    }

    /* 화면을 벗어난 통나무들을 지우는 함수 */
    override fun hideRemovedLogs(logs: List<FloatingObject>) {
        uiThread.launch {
            eachViewRemovingUpdate(logs, logTable)
        }
    }

    /* 점수를 알맞게 보여주는 함수 */
    override fun showScore(score: Int) {
        val scoreState = "Score : $score"
        uiThread.launch {
            binder.tvScore.text = scoreState
        }
    }

    /* 목숨 개수를 알맞게 보여주는 함수 */
    override fun showLife(life: Int) {
        val lifeState = "Life : $life"
        uiThread.launch {
            binder.tvLife.text = lifeState
        }
    }

    /* 게임 종료를 알리는 문장을 화면에 띄우는 함수 */
    override fun showGameEnd(score: Int) {
        val gameEndText = "Game Over!\nScore : $score"
        uiThread.launch {
            binder.tvGameEnd.visibility = View.VISIBLE
            binder.tvGameEnd.text = gameEndText
        }
    }


    //------------------------------------------------
    //함수 영역 (UI 적용 프로세스 내부 함수들)
    //

    /* FloatingObject 뷰를 화면에서 움직이는 함수 */
    private fun eachViewMovingUpdate(movingObjects: List<FloatingObject>, viewTable: MutableMap<Long, ImageView>){
        val iterator = movingObjects.iterator()
        var curObj: FloatingObject
        var curPos: Position
        var curView: ImageView?

        //데이터 개수만큼 업데이트 (객체와 대응하는 뷰 각각 가져와서 속성 갱신)
        while (iterator.hasNext()) {
            curObj = iterator.next()
            curPos = curObj.getPos()
            curView = viewTable[curObj.id]
            curView?.x = curPos.x
            curView?.y = curPos.y
        }
    }

    /* FloatingObject 뷰를 화면에서 지우는 함수 */
    private fun eachViewRemovingUpdate(removedObjects: List<FloatingObject>, viewTable: MutableMap<Long, ImageView>){
        val objectInList = removedObjects.iterator()
        var curId: Long

        //id에 해당하는 뷰를 테이블에서 삭제
        while (objectInList.hasNext()) {
            curId = objectInList.next().id
            binder.root.removeView(viewTable[curId])
            viewTable.remove(curId)
        }
    }

    /* 새로운 뱀 뷰를 만드는 함수 */
    private fun makeNewSnakeView(snake: Snake) {
        val newSnakeView = ImageView(context)
        viewCommonSetting(newSnakeView, R.drawable.snake, snake.size, snake.getPos())
    }

    /* 새로운 악어 뷰를 만드는 함수 */
    private fun makeNewCrocodileView(crocodile: FloatingObject): ImageView {
        val newCrocodileView = ImageView(context)
        val whichCrocodile = if(crocodile.velocity > 0)
                                R.drawable.crocodile_to_right
                             else
                                R.drawable.crocodile_to_left

        viewCommonSetting(newCrocodileView, whichCrocodile, crocodile.size, crocodile.getPos())
        return newCrocodileView
    }

    /* 새로운 통나무 뷰를 만드는 함수 */
    private fun makeNewLogView(log: FloatingObject): ImageView {
        val newLogView = ImageView(context)
        viewCommonSetting(newLogView, R.drawable.log, log.size, log.getPos())
        return newLogView
    }

    /* 이미지 뷰 생성 과정에서 적용하는 공통 속성을 담당하는 함수 */
    private fun viewCommonSetting(view: ImageView, resId: Int, size: ObjectSize, position: Position){
        //이미지 파일, 크기, 채우기 속성, 위치 순서대로 적용
        view.setImageResource(resId)
        view.layoutParams = FrameLayout.LayoutParams(size.width, size.height)
        view.scaleType = ImageView.ScaleType.FIT_XY
        view.x = position.x
        view.y = position.y

        //속성 적용이 끝났으면 화면에 추가
        binder.root.addView(view)
    }
}