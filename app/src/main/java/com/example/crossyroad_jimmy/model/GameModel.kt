package com.example.crossyroad_jimmy.model

import com.example.crossyroad_jimmy.model.floatingObject.Crocodile
import com.example.crossyroad_jimmy.model.floatingObject.FloatingObject
import com.example.crossyroad_jimmy.model.floatingObject.FloatingObjectFactory
import com.example.crossyroad_jimmy.model.floatingObject.Log

class GameModel(displayWidth: Int, displayHeight: Int) {
    //------------------------------------------------
    //상수 영역
    //

    //게임 내 주기적 업데이트의 빈도
    val updatePeriod = 100L


    //각 객체들의 크기 (충돌 여부 확인을 위해 사용)
    val snakeSize = ObjectSize((displayHeight * 0.08F).toInt(), (displayHeight * 0.08F).toInt())
    private val frogSize = ObjectSize((displayWidth * 0.1F).toInt(), (displayWidth * 0.1F).toInt())
    private val displaySize = ObjectSize(displayWidth, displayHeight)
    private val areaSize = ObjectSize(displayWidth, (displayHeight * 0.1F).toInt())

    //floatingObject 객체의 가로 길이 하한, 상한선 /
    private val floatingObjectMinWidth = (displayWidth * 0.3F).toInt()
    private val floatingObjectMaxWidth = (displayWidth * 0.45F).toInt()
    private val floatingObjectHeight = (displayHeight * 0.06F).toInt()

    //게임 속 각 구역의 position (화면 벗어남 판별 시 사용)
    private val areaX = 0F //모든 구역은 공통적인 x 좌표 가짐
    private val displayY = 0F
    private val destinationY = 0F
    private val grassY = displayHeight * 0.3F
    private val riversY = mutableListOf(
        displayHeight * 0.1F,
        displayHeight * 0.2F,
        displayHeight * 0.4F,
        displayHeight * 0.5F
    )

    //개구리 기본 속성
    private val frogJumpDistance = displayHeight * 0.1F
    private val frogDefaultX = displayWidth * 0.5F - frogSize.width / 2
    private val frogDefaultY = displayHeight * 0.65F - frogSize.height / 2

    //뱀 객체 개수 무작위 지정 시 최대값
    private val snakeCntMin = 1
    private val snakeCntMax = 2
    private val snakeSpawnRangeMin = 0F
    private val snakeSpawnRangeMax = (displayWidth - snakeSize.width).toFloat()

    //각 강의 흐름 속도 범위
    private val flowPeriodMin = 3000L
    private val flowPeriodMax = 4000L

    //floatingObject 생성 관련 상수 (방향, 속도, 생성 위치 순)
    private val floatingLeftDirection = -1
    private val floatingRightDirection = 1
    private val floatingSpeed = 25F
    private val floatingLeftStartXPosition = -floatingObjectMinWidth + 1F
    private val floatingRightStartXPosition = displayWidth - 1F
    private val floatingExtraY = displayHeight * 0.05F

    //------------------------------------------------
    //변수 영역
    //

    //각 floatingObject 생성 시 부여하는 id
    private var idValue = Long.MIN_VALUE


    //------------------------------------------------
    //인스턴스 영역
    //

    //개수가 바뀌지 않는(정적인) 인스턴스 혹은 리스트
    private val frog = Frog(frogJumpDistance, frogDefaultX, frogDefaultY, frogSize)
    private val rivers = listOf(
        River(floatingLeftStartXPosition, (riversY[0] + floatingExtraY), floatingRightDirection, RandomNumber.get(flowPeriodMin, flowPeriodMax)),
        River(floatingRightStartXPosition, (riversY[1] + floatingExtraY), floatingLeftDirection, RandomNumber.get(flowPeriodMin, flowPeriodMax)),
        River(floatingLeftStartXPosition, (riversY[2] + floatingExtraY), floatingRightDirection, RandomNumber.get(flowPeriodMin, flowPeriodMax)),
        River(floatingRightStartXPosition, (riversY[3] + floatingExtraY), floatingLeftDirection, RandomNumber.get(flowPeriodMin, flowPeriodMax))
    )
    private val snakes = mutableListOf<Snake>() //생성자에서 한 번 설정되고 변화하지 않음

    //개수가 바뀌는(동적인) 인스턴스 리스트
    private val crocodiles = mutableListOf<Crocodile>()
    private val logs = mutableListOf<Log>()


    //------------------------------------------------
    //생성자 영역
    //

    init{
        //뱀 개수 랜덤 지정
        val snakeCnt = RandomNumber.get(snakeCntMin ,snakeCntMax)

        //뱀 위치 랜덤 지정 및 리스트에 추가
        var snakeLocationX: Float
        for(i in 1..snakeCnt) {
            snakeLocationX = RandomNumber.get(snakeSpawnRangeMin, snakeSpawnRangeMax)
            snakes.add(Snake(snakeLocationX, grassY))
        }
    }

    //------------------------------------------------
    //함수 영역 (수시로 생성되는 floatingObject 객체들에게 id를 제공)
    //

    private fun objectId(): Long{
        //고유한 id를 주기 위한 업데이트
        idValue += 1L
        if(idValue == Long.MAX_VALUE)
            idValue = Long.MIN_VALUE

        return idValue
    }

    private fun floatingObjectSize(): ObjectSize{
        val floatingObjectWidth = RandomNumber.get(floatingObjectMinWidth, floatingObjectMaxWidth)
        return ObjectSize(floatingObjectWidth, floatingObjectHeight)
    }

    //------------------------------------------------
    //함수 영역 (데이터 전달)
    //

    /* 생성자에서 결정된 뱀 리스트를 전달하는 함수 */
    fun getSnakes(): List<Snake>{
        return snakes
    }

    /* 게임에서 자주 바뀌는 개구리의 위치를 전달하는 함수 */
    fun getFrogPosition(): Pair<Float, Float>{
        return Pair(frog.x, frog.y)
    }

    /* 화면에 남아있는 객체들의 리스트들을 전달하는 함수 */
    fun getCrocodiles(): List<Crocodile> {
        return crocodiles.toList()
    }
    fun getLogs(): List<Log>{
        return logs.toList()
    }

    /* 현재 개구리의 목숨 개수와 점수를 전달하는 함수 */
    fun getLife(): Int{
        return frog.life
    }
    fun getScore(): Int{
        return frog.score
    }


    //------------------------------------------------
    //함수 영역 (사용자 입력에 의한 갱신) - 개구리의 점프
    //


    /* 사용자가 점프 버튼을 누르면 실행하는 함수 */
    fun frogJump(){
        frog.floatingReset() //floating 여부 초기화 (새로 점프하므로)
        frog.jump() //새로 jump
        frogLocationProcess() //현 위치에 따른 다른 후속 과정 수행
    }

    //frogJump()의 내부 함수들

    /* 개구리가 점프한 위치를 체크하고 그에 맞는 후속 처리를 하는 함수 */
    private fun frogLocationProcess(){
        if(isFrogOnDestination()) //목적지
            processForFrogOnDestination()
        else if(isFrogOnGrass()) //풀밭
            processForFrogOnGrass()
        else if(isFrogOnRiver()) //강
            processForFrogOnRiver()
    }

    /* 개구리의 목적지 도착 여부를 true or false 값으로 알리는 함수 */
    private fun isFrogOnDestination(): Boolean{
        if(areTheyOverlapped(frog.x, frog.y, frogSize, areaX, destinationY, areaSize))
            return true
        return false
    }

    /* 개구리의 풀밭 도착 여부를 true or false 값으로 알리는 함수 */
    private fun isFrogOnGrass(): Boolean{
        if(areTheyOverlapped(frog.x, frog.y, frogSize, areaX, grassY, areaSize))
            return true
        return false
    }

    /* 개구리의 강 도착 여부를 true or false 값으로 알리는 함수 */
    private fun isFrogOnRiver(): Boolean{
        val riverYIterator = riversY.iterator()
        var eachRiverY: Float

        //강은 여러 개가 있으므로 모두 반복자를 통해 확인
        while(riverYIterator.hasNext()){
            eachRiverY = riverYIterator.next()
            if(areTheyOverlapped(frog.x, frog.y, frogSize, areaX, eachRiverY, areaSize))
                return true
        }
        return false
    }

    /* 목적지에 도착한 개구리를 위한 후속 처리를 하는 함수 */
    private fun processForFrogOnDestination(){
        frog.positionReset()
        frog.scoreIncrease()
    }

    /* 풀밭에 도착한 개구리를 위한 후속 처리를 하는 함수 */
    private fun processForFrogOnGrass(){
        val snakeIterator = snakes.iterator()
        var eachSnake: Snake

        //뱀과 접촉을 했다면 개구리 죽음 처리
        while(snakeIterator.hasNext()){
            eachSnake = snakeIterator.next()

            if(areTheyOverlapped(frog.x, frog.y, frogSize, eachSnake.x, eachSnake.y, snakeSize))
                frog.frogDead()
        }
    }

    /* 강에 도착한 개구리를 위한 후속 처리를 하는 함수 */
    private fun processForFrogOnRiver(){
        //개구리가 악어 위에 올라탔는지 확인
        if(frogBoardingCheck(crocodiles.iterator()))
            return

        //개구리가 통나무 위에 올라탔는지 확인
        if(frogBoardingCheck(logs.iterator()))
            return

        //떠다니는 물체(floatingObject)에 올라타지 못했다면 죽음으로 처리
        frog.frogDead()
    }

    private fun frogBoardingCheck(iterator: Iterator<FloatingObject>): Boolean{
        var eachFloatingObject: FloatingObject

        while(iterator.hasNext()){
            eachFloatingObject = iterator.next()
            if(eachFloatingObject.isFrogFloating(frog)){
                frog.floatingSetting(eachFloatingObject.velocity)
                return true
            }
        }
        return false
    }


    //------------------------------------------------
    //함수 영역 (주기적 갱신)
    //

    fun addFloatingObjects(): FloatingObjectData{
        val newCrocodiles = mutableListOf<Crocodile>()
        val newLogs = mutableListOf<Log>()

        val riverIterator = rivers.iterator()
        var eachRiver: River

        while(riverIterator.hasNext()){
            eachRiver = riverIterator.next()

            eachRiver.riverTimeIncrease(updatePeriod)
            if(eachRiver.isItTimeForNewFloatingObject()){
                val objectType = RandomNumber.get(FloatingObjectFactory.CROCODILE, FloatingObjectFactory.LOG)
                val floatingVelocity = floatingSpeed * eachRiver.direction
                val newFloatingObject
                    = FloatingObjectFactory.
                        create(objectType, objectId(), eachRiver.startX, eachRiver.startY, floatingVelocity, floatingObjectSize())

                if(objectType == FloatingObjectFactory.CROCODILE)
                    newCrocodiles.add(newFloatingObject as Crocodile)
                else
                    newLogs.add(newFloatingObject as Log)
            }
        }

        crocodiles.addAll(newCrocodiles)
        logs.addAll(newLogs)

        return FloatingObjectData(newCrocodiles, newLogs)
    }


    fun moveFloatingObjects(){
        frogFloatingUpdate()
        eachFloatingObjectMoveUpdate(crocodiles)
        eachFloatingObjectMoveUpdate(logs)
    }

    fun removeFloatingObjects(): FloatingObjectData {
        val removingCrocodiles = mutableListOf<Crocodile>()
        val removingLogs = mutableListOf<Log>()


        val crocodileIterator = crocodiles.iterator()
        val logIterator = logs.iterator()
        var eachFloatingObject: FloatingObject

        while (crocodileIterator.hasNext()) {
            eachFloatingObject = crocodileIterator.next()
            if (!areTheyOverlapped(eachFloatingObject.x, eachFloatingObject.y, eachFloatingObject.size, areaX, displayY, displaySize))
                removingCrocodiles.add(eachFloatingObject)
        }

        while (logIterator.hasNext()) {
            eachFloatingObject = logIterator.next()
            if (!areTheyOverlapped(eachFloatingObject.x, eachFloatingObject.y, eachFloatingObject.size, areaX, displayY, displaySize))
                removingLogs.add(eachFloatingObject)
        }

        crocodiles.removeAll(removingCrocodiles)
        logs.removeAll(removingLogs)

        return FloatingObjectData(removingCrocodiles, removingLogs)
    }

    /* 개구리가 floating 상태일 때 위치를 갱신하는 함수 */
    private fun frogFloatingUpdate(){
        //floating 상태가 아니면 함수 종료
        if(!frog.isFloating)
            return

        frog.frogFloating()

        //떠다니다가 화면 밖으로 나가면 개구리 죽음 처리
        if(!areTheyOverlapped(frog.x, frog.y, frogSize, areaX, displayY, displaySize))
            frog.frogDead()
    }

    /* 두 개체가 서로 겹치는지 확인하는 함수 */
    private fun areTheyOverlapped(x1: Float, y1:Float, size1: ObjectSize, x2: Float, y2: Float, size2: ObjectSize): Boolean {
        //2번째 개체의 우하단 꼭지점 좌표 계산 (범위 계산을 위해)
        val endX2 = x2 + size2.width
        val endY2 = y2 + size2.height

        //두 개체가 가로 범위에서 겹치는 부분이 있는가?
        if(x1 !in x2..endX2 && x1 + size1.width !in x2..endX2)
            return false

        //두 개체가 세로 범위에서 겹치는 부분이 있는가?
        if(y1 !in y2..endY2 && y1 + size1.height !in y2..endY2)
            return false

        return true
    }

    /* floatingObject 리스트의 모든 객체들 위치를 갱신하는 함수 */
    private fun eachFloatingObjectMoveUpdate(objects: List<FloatingObject>) {
        val it = objects.iterator()
        while (it.hasNext())
            it.next().positionUpdate()
    }
}