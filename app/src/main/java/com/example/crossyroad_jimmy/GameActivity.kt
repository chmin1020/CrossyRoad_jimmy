package com.example.crossyroad_jimmy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.crossyroad_jimmy.databinding.ActivityGameBinding
import com.example.crossyroad_jimmy.model.ObjectSize
import com.example.crossyroad_jimmy.model.Snake
import com.example.crossyroad_jimmy.model.floatingObject.Crocodile
import com.example.crossyroad_jimmy.model.floatingObject.FloatingObject
import com.example.crossyroad_jimmy.model.floatingObject.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity(), GameContract.View {

    private lateinit var binder:ActivityGameBinding

    private val presenter = GamePresenter()


    //탄환과 적 객체 대응 뷰들을 관리하는 테이블
    private val crocodileTable = mutableMapOf<Long,ImageView>()
    private val logTable = mutableMapOf<Long,ImageView>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binder = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binder.root)

        initListener()

        presenter.initSet(this, resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
        presenter.periodicUpdateTimerStart()
    }

    private fun initListener(){
        binder.btnMove.setOnClickListener {
            presenter.frogJump()
        }
    }

    override fun showSnakes(snakes: List<Snake>, size: ObjectSize) {
        val snakeIterator = snakes.iterator()

        while(snakeIterator.hasNext()){
            makeNewSnakeView(snakeIterator.next(), size)
        }
    }

    override fun showFrogPosition(x: Float, y: Float) {
        MainScope().launch {
            binder.frog.x = x
            binder.frog.y = y
        }
    }

    override fun showNewCrocodiles(crocodiles: List<Crocodile>, size: ObjectSize) {
        MainScope().launch {
            val crocodileIterator = crocodiles.iterator()
            var eachCrocodile: Crocodile

            while (crocodileIterator.hasNext()) {
                eachCrocodile = crocodileIterator.next()
                crocodileTable[eachCrocodile.id] = makeNewCrocodileView(eachCrocodile, size)
            }
        }
    }

    override fun showNewLogs(logs: List<Log>, size: ObjectSize) {
        MainScope().launch {
            val logIterator = logs.iterator()
            var eachLog: Log

            while (logIterator.hasNext()) {
                eachLog = logIterator.next()
                logTable[eachLog.id] = makeNewLogView(eachLog, size)
            }
        }
    }

    override fun showCrocodileMoving(crocodiles: List<Crocodile>) {
        MainScope().launch {
            eachViewMovingUpdate(crocodiles, crocodileTable)
        }
    }

    override fun showLogMoving(logs: List<Log>) {
        MainScope().launch {
            eachViewMovingUpdate(logs, logTable)
        }
    }

    override fun hideRemovedCrocodiles(crocodiles: List<Crocodile>) {
        MainScope().launch {
            eachViewRemovingUpdate(crocodiles, crocodileTable)
        }
    }

    override fun hideRemovedLogs(logs: List<Log>) {
        MainScope().launch {
            eachViewRemovingUpdate(logs, logTable)
        }
    }

    override fun showScore(score: Int) {
        TODO("Not yet implemented")
    }

    override fun showLife(Lift: Int) {
        TODO("Not yet implemented")
    }


    //------------------------------------------------
    //함수 영역 (UI 적용 프로세스 내부 함수들)
    //

    private fun eachViewMovingUpdate(movingObjects: List<FloatingObject>, viewTable: MutableMap<Long, ImageView>){
        val objectInList = movingObjects.iterator()
        var curObj: FloatingObject
        var curView: ImageView?

        //데이터 개수만큼 업데이트 (객체와 대응하는 뷰 각각 가져와서 속성 갱신)

        while (objectInList.hasNext()) {
            curObj = objectInList.next()
            curView = viewTable[curObj.id]
            curView?.x = curObj.x
            curView?.y = curObj.y
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

    private fun makeNewSnakeView(snake: Snake, size: ObjectSize) {
        val newSnakeView = ImageView(this)
        newSnakeView.setImageResource(R.drawable.snake)
        newSnakeView.layoutParams = FrameLayout.LayoutParams(size.width, size.height)
        newSnakeView.scaleType = ImageView.ScaleType.FIT_XY
        newSnakeView.x = snake.positionX
        newSnakeView.y = snake.positionY

        //속성 적용이 끝났으면 화면에 추가
        binder.root.addView(newSnakeView)
    }

    private fun makeNewCrocodileView(crocodile: Crocodile, size: ObjectSize): ImageView {
        val newCrocodileView = ImageView(this)
        if(crocodile.velocity > 0)
            newCrocodileView.setImageResource(R.drawable.crocodile_to_right)
        else
            newCrocodileView.setImageResource(R.drawable.crocodile_to_left)

        newCrocodileView.layoutParams = FrameLayout.LayoutParams(size.width, size.height)
        newCrocodileView.scaleType = ImageView.ScaleType.FIT_XY
        newCrocodileView.x = crocodile.x
        newCrocodileView.y = crocodile.y

        //속성 적용이 끝났으면 화면에 추가
        binder.root.addView(newCrocodileView)
        return newCrocodileView
    }

    private fun makeNewLogView(log: Log, size: ObjectSize): ImageView {
        val newLogView = ImageView(this)
        newLogView.setImageResource(R.drawable.log)

        newLogView.layoutParams = FrameLayout.LayoutParams(size.width, size.height)
        newLogView.scaleType = ImageView.ScaleType.FIT_XY
        newLogView.x = log.x
        newLogView.y = log.y

        //속성 적용이 끝났으면 화면에 추가
        binder.root.addView(newLogView)
        return newLogView
    }


}