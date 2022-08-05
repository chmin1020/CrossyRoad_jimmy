package com.example.crossyroad_jimmy.model

import com.example.crossyroad_jimmy.model.floatingObject.Crocodile
import com.example.crossyroad_jimmy.model.floatingObject.FloatingObject
import com.example.crossyroad_jimmy.model.floatingObject.Log

class GameModel(displayWidth: Int, displayHeight: Int) {
    //------------------------------------------------
    //상수 영역
    //

    //게임 내 객체(Bullet, Enemy)의 위치 변경이 발생하는 빈도
    val updatePeriod = 100L

    private val flowPeriodLowerPoint = 3000L
    private val flowPeriodUpperPoint = 4000L

    private val floatingSpeed = 25F

    private val snakeMaxCnt = 2

    private val yPosOfRiver1 = displayHeight * 0.15F
    private val yPosOfRiver2 = displayHeight * 0.25F
    private val yPosOfRiver3 = displayHeight * 0.45F
    private val yPosOfRiver4 = displayHeight * 0.55F

    private val yPosOfGrass = displayHeight * 0.3F

    val snakeSize = ObjectSize((displayHeight * 0.08F).toInt(), (displayHeight * 0.08F).toInt())
    val floatingObjectSize = ObjectSize((displayWidth * 0.4F).toInt(), (displayHeight * 0.06F).toInt())
    private val frogSize = ObjectSize((displayWidth * 0.1F).toInt(), (displayWidth * 0.1F).toInt())
    private val displaySize = ObjectSize(displayWidth, displayHeight)

    //게임 화면의 position (화면 벗어남 판별 시 사용)
    private val displayX = 0F
    private val displayY = 0F


    //------------------------------------------------
    //변수 영역
    //

    private var idValue = Long.MIN_VALUE

    private val frog = Frog(displayHeight * 0.1F, displayWidth * 0.5F - frogSize.width / 2, displayHeight * 0.65F - frogSize.height / 2)

    private val crocodiles = mutableListOf<Crocodile>()
    private val logs = mutableListOf<Log>()
    private val snakes = mutableListOf<Snake>()


    private val rivers = listOf(
        River(-floatingObjectSize.width.toFloat() + 1F, yPosOfRiver1, 1, RandomNumber.get(flowPeriodLowerPoint, flowPeriodUpperPoint)),
        River(displayWidth.toFloat() - 1F, yPosOfRiver2, -1, RandomNumber.get(flowPeriodLowerPoint, flowPeriodUpperPoint)),
        River(-floatingObjectSize.width.toFloat() + 1F, yPosOfRiver3, 1, RandomNumber.get(flowPeriodLowerPoint, flowPeriodUpperPoint)),
        River(displayWidth.toFloat() -1F, yPosOfRiver4, -1, RandomNumber.get(flowPeriodLowerPoint, flowPeriodUpperPoint))
    )



    // 생성자
    init{
        //뱀 객체 개수 및 위치 랜덤 지정
        val snakeCnt = RandomNumber.get(1 ,snakeMaxCnt)
        for(i in 1..snakeCnt)
            snakes.add(Snake(RandomNumber.get(0F, (displayWidth - snakeSize.width).toFloat()),yPosOfGrass + displayHeight * 0.01F))
    }


    //새로 만들어지는 적과 탄환에게 고유한 아이디를 부여
    private fun objectId(): Long{
        idValue += 1L
        if(idValue == Long.MAX_VALUE)
            idValue = Long.MIN_VALUE

        return idValue
    }

    //------------------------------------------------
    //함수 영역 (데이터 전달)
    //

    fun getFrogPosition(): Pair<Float, Float>{
        return Pair(frog.currentX, frog.currentY)
    }

    fun getSnakes(): List<Snake>{
        return snakes
    }

    /* 화면에 남아있는 객체들의 리스트 */
    fun getCrocodiles(): List<Crocodile> {
        return crocodiles.toList()
    }
    fun getLogs(): List<Log>{
        return logs.toList()
    }


    //------------------------------------------------
    //함수 영역 (사용자 입력에 의한 갱신)
    //

    fun frogJump(): Pair<Float, Float>{
        frog.frontJump()


        // - 죽었는가?
        // - 목적지에 도착했는가?
        // - floating에 올라탔는가?


        return getFrogPosition()
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
        eachMoveUpdate(crocodiles)
        eachMoveUpdate(logs)
    }

    fun removeFloatingObjects(): FloatingObjectData {
        val removingCrocodiles = mutableListOf<Crocodile>()
        val removingLogs = mutableListOf<Log>()


        val crocodileIterator = crocodiles.iterator()
        val logIterator = logs.iterator()
        var eachFloatingObject: FloatingObject

        while (crocodileIterator.hasNext()) {
            eachFloatingObject = crocodileIterator.next()
            if (!areTheyOverlapped(eachFloatingObject.x, eachFloatingObject.y, floatingObjectSize, displayX, displayY, displaySize))
                removingCrocodiles.add(eachFloatingObject)
        }

        while (logIterator.hasNext()) {
            eachFloatingObject = logIterator.next()
            if (!areTheyOverlapped(eachFloatingObject.x, eachFloatingObject.y, floatingObjectSize, displayX, displayY, displaySize))
                removingLogs.add(eachFloatingObject)
        }

        crocodiles.removeAll(removingCrocodiles)
        logs.removeAll(removingLogs)

        return FloatingObjectData(removingCrocodiles, removingLogs)
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


    private fun eachMoveUpdate(objects: List<FloatingObject>) {
        val it = objects.iterator()
        while (it.hasNext())
            it.next().positionUpdate()
    }


}