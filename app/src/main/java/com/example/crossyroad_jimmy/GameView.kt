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

/**
 *     View 인터페이스를 구현하는 MVP 속 View 역할을 하는 클래스
 *     control 부분이 아닌, view 위치 갱신이나 추가 및 삭제같은 drawing 부분은 이 클래스가 담당한다.
 *     UI 수정을 위해서는 activity context 가 필요하므로 이를 setting 때 넘겨 받는다.
 */
class GameView(private val context: Context, private val gamePresenter: GameContract.Presenter): GameContract.View {
    //------------------------------------------------
    // 변수 영역
    //

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
    fun viewInitSetting(): ViewBinding {
        binder = ActivityGameBinding.inflate((context as Activity).layoutInflater)
        initListener()
        return binder
    }

    /* 뷰 이벤트 세팅 함수 */
    private fun initListener(){
        //점프 버튼
        binder.btnJump.setOnClickListener {
            gamePresenter.frogJump()
        }
    }


    //------------------------------------------------
    //함수 영역 (GameContract.View 오버라이딩)
    //

    /* 무작위 설정된 뱀들을 그리는 함수 */
    override fun showSnakes(snakes: List<Snake>) {
        val snakeIterator = snakes.iterator()

        while(snakeIterator.hasNext()){
            makeNewSnakeView(snakeIterator.next())
        }
    }

    /* 개구리를 위치에 맞게 그리는 함수 */
    override fun showFrogPosition(x: Float, y: Float) {
        uiThread.launch {
            binder.frog.x = x
            binder.frog.y = y
        }
    }

    /* 새로운 악어들을 그리는 함수 */
    override fun showNewCrocodiles(crocodiles: Map<Long, Crocodile>) {
        uiThread.launch {
            crocodiles.forEach{ crocodileTable[it.key] = makeNewCrocodileView(it.value) }
        }
    }

    /* 새로운 통나무들을 그리는 함수 */
    override fun showNewLogs(logs: Map<Long, Log>) {
        uiThread.launch {
            logs.forEach { logTable[it.key] = makeNewLogView(it.value) }
        }
    }

    /* 악어를 위치에 맞게 이동시켜서 그리는 함수 */
    override fun showCrocodileMoving(crocodiles: Map<Long, Crocodile>) {
        uiThread.launch {
            eachViewMovingUpdate(crocodiles, crocodileTable)
        }
    }

    /* 통나무를 위치에 맞게 이동시켜서 그리는 함수 */
    override fun showLogMoving(logs: Map<Long, Log>) {
        uiThread.launch {
            eachViewMovingUpdate(logs, logTable)
        }
    }

    /* 화면을 벗어난 악어들을 지우는 함수 */
    override fun hideRemovedCrocodiles(crocodiles: List<Long>) {
        uiThread.launch {
            eachViewRemovingUpdate(crocodiles, crocodileTable)
        }
    }

    /* 화면을 벗어난 통나무들을 지우는 함수 */
    override  fun hideRemovedLogs(logs: List<Long>) {
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
    private fun eachViewMovingUpdate(floatingObjects: Map<Long, FloatingObject>, viewTable: MutableMap<Long, ImageView>){
        var curPos: Position
        var curView: ImageView?

        //데이터 개수만큼 업데이트 (객체와 대응하는 뷰를 id를 이용해 가져와서 속성 갱신)
        floatingObjects.forEach{
            curView = viewTable[it.key]
            curPos = it.value.getPos()
            curView?.x = curPos.x
            curView?.y = curPos.y
        }
    }

    /* FloatingObject 뷰를 화면에서 지우는 함수 */
    private fun eachViewRemovingUpdate(removedObjects: List<Long>, viewTable: MutableMap<Long, ImageView>){
        //id에 해당하는 뷰를 화면과 테이블에서 삭제
        removedObjects.forEach { id ->
            binder.root.removeView(viewTable[id])
            viewTable.remove(id)
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

        //악어의 이동 방향에 따른 이미지 선택
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