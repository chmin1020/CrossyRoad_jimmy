package com.example.crossyroad_jimmy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

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