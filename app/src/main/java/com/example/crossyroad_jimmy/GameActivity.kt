package com.example.crossyroad_jimmy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 *     액티비티는 View - Presenter 사이를 연결하는 컴포넌트
 *     리스너 세팅을 마친 뷰 바인딩 객체와 컨텍스트로 view 세팅을 완료하고,
 *     이 뷰와 게임화면 크기로 presenter 세팅을 완료하여 둘을 연결한다.
 */
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

    /* onCreate()에서는 view, presenter 객체의 initSetting 실시 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View 초기 세팅, 이후 뷰바인딩 객체 받아와서 적용
        setContentView(view.viewInitSetting().root)

        //Presenter 초기 세팅, 뷰와 적절한 모델을 가져오기 위한 화면 크기를 인자로
        presenter.gameInitSetting(view, resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
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