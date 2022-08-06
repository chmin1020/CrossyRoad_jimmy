package com.example.crossyroad_jimmy.model

import com.example.crossyroad_jimmy.model.floatingObject.Crocodile
import com.example.crossyroad_jimmy.model.floatingObject.FloatingObject
import com.example.crossyroad_jimmy.model.floatingObject.Log
import kotlin.math.log

class GameModel(displayWidth: Int, displayHeight: Int) {
    //------------------------------------------------
    //상수 영역
    //

    //게임 내 주기적 업데이트의 빈도
    val updatePeriod = 100L

    //각 강의 흐름 속도 범위
    private val flowPeriodMin = 3000L
    private val flowPeriodMax = 4000L

    //floatingObject 객체들의 속도
    private val floatingSpeed = 25F

    //뱀 객체 개수 무작위 지정 시 최대값
    private val snakeCntMin = 1
    private val snakeCntMax = 2

    private val yPosOfRiver1 = displayHeight * 0.15F
    private val yPosOfRiver2 = displayHeight * 0.25F
    private val yPosOfRiver3 = displayHeight * 0.45F
    private val yPosOfRiver4 = displayHeight * 0.55F

    //악어는 등 범위를 파악하기 위한 상수 (주둥이의 길이, 등의 길이)
    private val crocodileSnoutWidth = (displayWidth * 0.12F).toInt()
    private val crocodileBackWidth = (displayWidth * 0.24F).toInt()

    //각 객체들의 크기 (충돌 여부 확인을 위해 사용)
    val snakeSize = ObjectSize((displayHeight * 0.08F).toInt(), (displayHeight * 0.08F).toInt())
    val floatingObjectSize = ObjectSize((displayWidth * 0.36F).toInt(), (displayHeight * 0.06F).toInt())
    private val frogSize = ObjectSize((displayWidth * 0.1F).toInt(), (displayWidth * 0.1F).toInt())
    private val crocodileBackSize = ObjectSize(crocodileBackWidth,floatingObjectSize.height)
    private val displaySize = ObjectSize(displayWidth, displayHeight)
    private val areaSize = ObjectSize(displayWidth, (displayHeight * 0.1F).toInt())

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

    //------------------------------------------------
    //변수 영역
    //

    //각 floatingObject 생성 시 부여하는 id
    private var idValue = Long.MIN_VALUE

    //------------------------------------------------
    //인스턴스 영역
    //

    //개수가 바뀌지 않는(정적인) 인스턴스 혹은 리스트
    private val frog = Frog(displayHeight * 0.1F, displayWidth * 0.5F - frogSize.width / 2, displayHeight * 0.65F - frogSize.height / 2)
    private val rivers = listOf(
        River(-floatingObjectSize.width.toFloat() + 1F, yPosOfRiver1, 1, RandomNumber.get(flowPeriodMin, flowPeriodMax)),
        River(displayWidth.toFloat() - 1F, yPosOfRiver2, -1, RandomNumber.get(flowPeriodMin, flowPeriodMax)),
        River(-floatingObjectSize.width.toFloat() + 1F, yPosOfRiver3, 1, RandomNumber.get(flowPeriodMin, flowPeriodMax)),
        River(displayWidth.toFloat() -1F, yPosOfRiver4, -1, RandomNumber.get(flowPeriodMin, flowPeriodMax))
    )
    private val snakes = mutableListOf<Snake>() //생성자에서 한 번 설정되고 변화하지 않음


    //개수가 바뀌는(동적인) 인스턴스 리스트
    private val crocodiles = mutableListOf<Crocodile>()
    private val logs = mutableListOf<Log>()

    //------------------------------------------------
    //생성자 영역
    //

    init{
        //뱀 객체 개수 및 위치 랜덤 지정
        val snakeCnt = RandomNumber.get(snakeCntMin ,snakeCntMax)
        for(i in 1..snakeCnt)
            snakes.add(Snake(RandomNumber.get(0F, (displayWidth - snakeSize.width).toFloat()),grassY))
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
    //함수 영역 (사용자 입력에 의한 갱신)
    //

    fun frogJump(){
        frog.frontJump()

        // - 목적지에 도착했는가?
        if(areTheyOverlapped(frog.x, frog.y, frogSize, areaX, destinationY, areaSize)) {
            frog.positionReset()
            frog.scoreIncrease()
        }

        // - 뱀에게 죽었는가?
        if(areTheyOverlapped(frog.x, frog.y, frogSize, areaX, grassY, areaSize)){
            val snakeIterator = snakes.iterator()
            var eachSnake: Snake

            while(snakeIterator.hasNext()){
                eachSnake = snakeIterator.next()

                if(areTheyOverlapped(frog.x, frog.y, frogSize, eachSnake.x, eachSnake.y, snakeSize)){
                    frog.frogDead()
                    frog.positionReset()
                }
            }
        }

        val riverYIterator = riversY.iterator()
        var eachRiverY: Float
        //강에서 죽었는가
        frog.floatingReset()
        while(riverYIterator.hasNext()){
            eachRiverY = riverYIterator.next()
            if(areTheyOverlapped(frog.x, frog.y, frogSize, areaX, eachRiverY, areaSize)){
                val crocodileIterator = crocodiles.iterator()
                val logIterator = logs.iterator()
                var eachCrocodile: Crocodile
                var eachLog: Log
                var isDead = true

                var crocodileBackX: Float
                while(crocodileIterator.hasNext()){
                    eachCrocodile = crocodileIterator.next()

                    crocodileBackX = eachCrocodile.x
                    if(eachCrocodile.velocity < 0)
                        crocodileBackX += crocodileSnoutWidth

                    if(areTheyOverlapped(frog.x, frog.y, frogSize, crocodileBackX, eachCrocodile.y, crocodileBackSize)){
                        frog.floatingSetting(eachCrocodile.velocity)
                        isDead = false
                    }
                }

                while(logIterator.hasNext()){
                    eachLog = logIterator.next()
                    if(areTheyOverlapped(frog.x, frog.y, frogSize, eachLog.x, eachLog.y, floatingObjectSize)){
                        frog.floatingSetting(eachLog.velocity)
                        isDead = false
                    }
                }

                if(isDead){
                    frog.positionReset()
                    frog.frogDead()
                }

                break
            }
        }
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
                val newFloatingObject
                    = FloatingObjectFactory.create(objectType, objectId(), eachRiver.entranceX, eachRiver.entranceY, floatingSpeed * eachRiver.direction)


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
            if (!areTheyOverlapped(eachFloatingObject.x, eachFloatingObject.y, floatingObjectSize, areaX, displayY, displaySize))
                removingCrocodiles.add(eachFloatingObject)
        }

        while (logIterator.hasNext()) {
            eachFloatingObject = logIterator.next()
            if (!areTheyOverlapped(eachFloatingObject.x, eachFloatingObject.y, floatingObjectSize, areaX, displayY, displaySize))
                removingLogs.add(eachFloatingObject)
        }

        crocodiles.removeAll(removingCrocodiles)
        logs.removeAll(removingLogs)

        return FloatingObjectData(removingCrocodiles, removingLogs)
    }


    private fun frogFloatingUpdate(){
        if(!frog.isFloating)
            return
        frog.frogFloating()

        if(!areTheyOverlapped(frog.x, frog.y, frogSize, areaX, displayY, displaySize)){
            frog.floatingReset()
            frog.positionReset()
            frog.frogDead()
        }
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


    private fun eachFloatingObjectMoveUpdate(objects: List<FloatingObject>) {
        val it = objects.iterator()
        while (it.hasNext())
            it.next().positionUpdate()
    }
}