package com.example.crossyroad_jimmy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.os.persistableBundleOf
import com.example.crossyroad_jimmy.databinding.ActivityGameBinding
import com.example.crossyroad_jimmy.model.ObjectSize
import com.example.crossyroad_jimmy.model.Snake
import com.example.crossyroad_jimmy.model.floatingObject.Crocodile
import com.example.crossyroad_jimmy.model.floatingObject.FloatingObject
import com.example.crossyroad_jimmy.model.floatingObject.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {
    //------------------------------------------------
    //instance 영역
    //

    //view & presenter
    private val presenter = GamePresenter()
    private val view = GameView(this, presenter)


    //------------------------------------------------
    //생명주기 함수 영역
    //

    /* onCreate()에서는 view binding, presenter, listeners 세팅 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.initSet().root)
        presenter.initSet(view, resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
    }

    /* onResume()에서는 주기적 갱신 타이머 시작 */
    override fun onResume() {
        super.onResume()
        presenter.periodicUpdateTimerStart()
    }

    /* onPause()에서는 주기적 갱신 타이머 종료 */
    override fun onPause() {
        super.onPause()
        presenter.periodicUpdateTimerStop()
    }
}