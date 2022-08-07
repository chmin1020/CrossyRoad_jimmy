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

    fun initSet(): ViewBinding {
        binder = ActivityGameBinding.inflate((context as Activity).layoutInflater)
        initListener()
        return binder
    }

    private fun initListener(){
        binder.btnMove.setOnClickListener {
            presenter.frogJump()
        }
    }


    override fun showFrogPosition(x: Float, y: Float) {
        uiThread.launch {
            binder.frog.x = x
            binder.frog.y = y
        }
    }

    override fun showSnakes(snakes: List<Snake>) {
        val snakeIterator = snakes.iterator()

        while(snakeIterator.hasNext()){
            makeNewSnakeView(snakeIterator.next())
        }
    }

    override fun showNewCrocodiles(crocodiles: List<Crocodile>) {
        uiThread.launch {
            val crocodileIterator = crocodiles.iterator()
            var eachCrocodile: Crocodile

            while (crocodileIterator.hasNext()) {
                eachCrocodile = crocodileIterator.next()
                crocodileTable[eachCrocodile.id] = makeNewCrocodileView(eachCrocodile)
            }
        }
    }

    override fun showNewLogs(logs: List<Log>) {
        uiThread.launch {
            val logIterator = logs.iterator()
            var eachLog: Log

            while (logIterator.hasNext()) {
                eachLog = logIterator.next()
                logTable[eachLog.id] = makeNewLogView(eachLog)
            }
        }
    }

    override fun showCrocodileMoving(crocodiles: List<Crocodile>) {
        uiThread.launch {
            eachViewMovingUpdate(crocodiles, crocodileTable)
        }
    }

    override fun showLogMoving(logs: List<Log>) {
        uiThread.launch {
            eachViewMovingUpdate(logs, logTable)
        }
    }

    override fun hideRemovedCrocodiles(crocodiles: List<Crocodile>) {
        uiThread.launch {
            eachViewRemovingUpdate(crocodiles, crocodileTable)
        }
    }

    override fun hideRemovedLogs(logs: List<Log>) {
        uiThread.launch {
            eachViewRemovingUpdate(logs, logTable)
        }
    }

    override fun showScore(score: Int) {
        val scoreState = "Score : $score"
        uiThread.launch {
            binder.tvScore.text = scoreState
        }
    }

    override fun showLife(life: Int) {
        val lifeState = "Life : $life"
        uiThread.launch {
            binder.tvLife.text = lifeState
        }
    }

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

    private fun eachViewMovingUpdate(movingObjects: List<FloatingObject>, viewTable: MutableMap<Long, ImageView>){
        val objectInList = movingObjects.iterator()
        var curObj: FloatingObject
        var curPos: Position
        var curView: ImageView?

        //데이터 개수만큼 업데이트 (객체와 대응하는 뷰 각각 가져와서 속성 갱신)
        while (objectInList.hasNext()) {
            curObj = objectInList.next()
            curPos = curObj.getPos()
            curView = viewTable[curObj.id]
            curView?.x = curPos.x
            curView?.y = curPos.y
        }
    }

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

    private fun makeNewSnakeView(snake: Snake) {
        makeNewView(R.drawable.snake, snake.size, snake.getPos())
    }

    private fun makeNewCrocodileView(crocodile: Crocodile): ImageView {
        val whichCrocodile = if(crocodile.velocity > 0)
                                R.drawable.crocodile_to_right
                             else
                                R.drawable.crocodile_to_left

        return makeNewView(whichCrocodile, crocodile.size, crocodile.getPos())
    }

    private fun makeNewLogView(log: Log): ImageView {
        return makeNewView(R.drawable.log, log.size, log.getPos())
    }

    private fun makeNewView(resId: Int, size: ObjectSize, position: Position): ImageView{
        val newView = ImageView(context)
        newView.setImageResource(resId)
        newView.layoutParams = FrameLayout.LayoutParams(size.width, size.height)
        newView.scaleType = ImageView.ScaleType.FIT_XY
        newView.x = position.x
        newView.y = position.y

        //속성 적용이 끝났으면 화면에 추가
        binder.root.addView(newView)
        return newView
    }
}