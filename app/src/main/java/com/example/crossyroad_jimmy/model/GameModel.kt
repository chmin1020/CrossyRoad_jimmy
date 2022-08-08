package com.example.crossyroad_jimmy.model

import com.example.crossyroad_jimmy.model.floatingObject.*

/**
 *     <메인 게임 모델>
 *         세부 모델 객체들을 이용하여 실제 게임 진행 여부를 갱신 및 저장하는 model class.
 *         Presenter 내부에서 세부 모델들을 알지 못한 상태로 각각을 사용할 수 있도록 하는 역할.
 *         데이터 전달, 사용자 요청의 의한 갱신, 자동 갱신 등의 역할에 따라 함수가 구성됨
 */
class GameModel(displayWidth: Int, displayHeight: Int) {
    //------------------------------------------------
    //상수 영역
    //

    //게임 내 주기적 업데이트의 빈도
    val updatePeriod = 100L

    //화면과 게임 내 구역들의 크기(모두 동일)
    private val displaySize = ObjectSize(displayWidth, displayHeight)
    private val areaSize = ObjectSize(displayWidth, (displayHeight * 0.1F).toInt())

    //각 강의 흐름 속도 범위
    private val flowPeriodMin = 3000L
    private val flowPeriodMax = 4000L

    //floatingObject 객체의 가로 길이 하한, 상한선 / 세로 길이
    private val floatingObjectMinWidth = (displayWidth * 0.3F).toInt()
    private val floatingObjectMaxWidth = (displayWidth * 0.45F).toInt()
    private val floatingObjectHeight = (displayHeight * 0.06F).toInt()

    //floatingObject 생성 관련 상수 (방향, 생성 위치 순)
    private val floatingLeftDirection = -1
    private val floatingRightDirection = 1
    private val floatingLeftStartX = -floatingObjectMinWidth + 1F
    private val floatingRightStartX = displayWidth - 1F
    private val extraYForSpawningFloatingObject = displayHeight * 0.05F

    //게임 속 각 구역의 position (화면 벗어남 판별 시 사용)
    private val areaX = 0F //모든 구역은 공통적인 x 좌표 가짐
    private val displayPosition = Position(areaX, 0F)
    private val destinationPosition = Position(areaX, 0F)
    private val grassPosition = Position(areaX, displayHeight * 0.3F)
    private val riverPositions = listOf(
        Position(areaX, displayHeight * 0.1F),
        Position(areaX, displayHeight * 0.2F),
        Position(areaX, displayHeight * 0.4F),
        Position(areaX, displayHeight * 0.5F)
    )

    //floating object 객체들의 spawning 위치 (spawnManager 인자 역할)
    private val spawnPositions = listOf(
        Position(floatingLeftStartX, riverPositions[0].y + extraYForSpawningFloatingObject),
        Position(floatingRightStartX, riverPositions[1].y + extraYForSpawningFloatingObject),
        Position(floatingLeftStartX, riverPositions[2].y + extraYForSpawningFloatingObject),
        Position(floatingRightStartX, riverPositions[3].y + extraYForSpawningFloatingObject)
    )

    //개구리 기본 속성
    private val frogJumpDistance = displayHeight * 0.1F
    private val frogSize = ObjectSize((displayWidth * 0.1F).toInt(), (displayWidth * 0.1F).toInt())
    private val frogDefaultPosition =
        Position(displayWidth * 0.5F - frogSize.width / 2, displayHeight * 0.65F - frogSize.height / 2)

    //뱀 관련 속성 값
    private val snakeCntMin = 1
    private val snakeCntMax = 2
    private val snakeSize = ObjectSize((displayHeight * 0.08F).toInt(), (displayHeight * 0.08F).toInt())
    private val snakeSpawnRangeMin = 0F
    private val snakeSpawnRangeMax = (displayWidth - snakeSize.width).toFloat()

    //적절한 floatingObject 생성을 위한 상수
    private val typeCrocodile = 0
    private val typeLog = 1


    //------------------------------------------------
    //변수 영역
    //

    //각 floatingObject 생성 시 부여하는 id
    private var idValue = Long.MIN_VALUE


    //------------------------------------------------
    //인스턴스 영역
    //

    //개수가 바뀌지 않는(정적인) 인스턴스 혹은 리스트
    private val frog = Frog(frogJumpDistance, frogDefaultPosition, frogSize)
    private val spawnManagers = listOf(
        FloatObjectSpawnManager(spawnPositions[0], floatingRightDirection, RandomNumber.get(flowPeriodMin, flowPeriodMax)),
        FloatObjectSpawnManager(spawnPositions[1], floatingLeftDirection, RandomNumber.get(flowPeriodMin, flowPeriodMax)),
        FloatObjectSpawnManager(spawnPositions[2], floatingRightDirection, RandomNumber.get(flowPeriodMin, flowPeriodMax)),
        FloatObjectSpawnManager(spawnPositions[3], floatingLeftDirection, RandomNumber.get(flowPeriodMin, flowPeriodMax))
    )
    private val snakes = mutableListOf<Snake>() //생성자에서 한 번 설정되고 변화하지 않음

    //개수가 바뀌는(동적인) 인스턴스 테이블(id, 객체)
    private val crocodileTable = mutableMapOf<Long, Crocodile>()
    private val logTable = mutableMapOf<Long, Log>()


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
            snakes.add(Snake(Position(snakeLocationX, grassPosition.y), snakeSize))
        }
    }


    //------------------------------------------------
    //함수 영역 (값이 항상 달라지는 변수를 얻기 위한 load 함수)
    //

    /* FloatingObject 생성 시 부여하는 고유 id */
    private fun loadObjectId(): Long{
        //새 id를 주기 위한 업데이트
        idValue += 1L
        if(idValue == Long.MAX_VALUE)
            idValue = Long.MIN_VALUE

        return idValue
    }

    /* FloatingObject 생성 시 부여하는 무작위 객체 크기 */
    private fun loadFloatingObjectSize(): ObjectSize{
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
    fun getFrogPosition(): Position{
        return frog.getPos()
    }

    /* 화면에 남아있는 객체들의 테이블을 전달하는 함수 */
    fun getCrocodiles(): Map<Long, Crocodile> {
        return crocodileTable.toMap()
    }
    fun getLogs():Map<Long, Log>{
        return logTable.toMap()
    }

    /* 현재 개구리의 목숨 개수와 점수를 전달하는 함수 */
    fun getLife(): Int{
        return frog.life
    }
    fun getScore(): Int{
        return frog.score
    }


    //------------------------------------------------
    //함수 영역 (사용자 입력에 의한 갱신 - 개구리의 점프)
    //

    /* 사용자가 점프 버튼을 누르면 실행하는 함수 */
    fun frogJump(){
        frog.floatingReset() //floating 여부 초기화 (새로 점프하므로)
        frog.jump() //새로 jump
        frogLocationProcess() //현 위치에 따른 다른 후속 과정 수행
    }


    /* 개구리가 점프한 위치를 체크하고 그에 맞는 후속 처리를 하는 함수 */
    private fun frogLocationProcess(){
        if(isFrogOnDestination()) //목적지
            processForFrogOnDestination()
        else if(isFrogOnGrass()) //풀밭
            processForFrogOnGrass()
        else if(isFrogOnRiver()) //강
            processForFrogOnRiver()
    }


    //개구리의 현재 영역 체크

    /* 개구리의 목적지 도착 여부를 true or false 값으로 알리는 함수 */
    private fun isFrogOnDestination(): Boolean{
        if(areTheyOverlapped(frog.getPos(), frogSize, destinationPosition, areaSize))
            return true
        return false
    }

    /* 개구리의 풀밭 도착 여부를 true or false 값으로 알리는 함수 */
    private fun isFrogOnGrass(): Boolean{
        if(areTheyOverlapped(frog.getPos(), frogSize, grassPosition, areaSize))
            return true
        return false
    }

    /* 개구리의 강 도착 여부를 true or false 값으로 알리는 함수 */
    private fun isFrogOnRiver(): Boolean{
        //모든 강 위치와 개구리 위치를 비교하며 확인
        riverPositions.forEach { eachPosition ->
            if(areTheyOverlapped(frog.getPos(), frogSize, eachPosition, areaSize))
                return true
        }
        return false
    }


    //현재 영역에 따른 후속 처리 과정

    /* 목적지에 도착한 개구리를 위한 후속 처리를 하는 함수 (위치 초기화, 점수 증가) */
    private fun processForFrogOnDestination(){
        frog.positionReset()
        frog.scoreIncrease()
    }

    /* 풀밭에 도착한 개구리를 위한 후속 처리를 하는 함수 (뱀과의 충돌 여부 확인) */
    private fun processForFrogOnGrass(){
        //뱀과 접촉을 했다면 개구리 죽음 처리
        snakes.forEach { eachSnake ->
            if(areTheyOverlapped(frog.getPos(), frogSize, eachSnake.getPos(), snakeSize))
                frog.frogDead()
        }
    }

    /* 강에 도착한 개구리를 위한 후속 처리를 하는 함수 (악어 또는 통나무 위에 안 올라가면 죽음 처리) */
    private fun processForFrogOnRiver(){
        //개구리가 악어 또는 통나무 위에 올라탔는지 확인
        if(frogBoardingCheck(crocodileTable.values.toList()) || frogBoardingCheck(logTable.values.toList()))
            return

        //못 올라탔다면 죽음으로 처리
        frog.frogDead()
    }

    /* 강에 도착한 개구리가 다른 물체에 탑승했는지 확인하는 함수 */
    private fun frogBoardingCheck(floatingObjects: List<FloatingObject>): Boolean{
        //모든 floatingObject - frog 탑승 관계 체크 및 설정
        floatingObjects.forEach { eachFloatingObject ->
            if(eachFloatingObject.isFrogFloating(frog)) {
                frog.floatingSetting(eachFloatingObject.velocity)
                return true
            }
        }
        return false
    }


    //------------------------------------------------
    //함수 영역 (주기적 갱신 - floatingObject 추가)
    //

    /* 떠 있는 객체를 새롭게 추가하는 함수 */
    fun addFloatingObjects(): FloatingObjectTables {
        //추가할 객체들 선별
        val addedFloatingObjects = addFloatingObjectsByManagers()

        //실제 테이블에 추가
        crocodileTable.putAll(addedFloatingObjects.crocodiles)
        logTable.putAll(addedFloatingObjects.logs)

        return addedFloatingObjects
    }

    /* 각 강의 spawn 대기 시간을 살피며, 시간이 다 된 강에 객체를 추가하는 함수 */
    private fun addFloatingObjectsByManagers(): FloatingObjectTables {
        val newCrocodiles = mutableMapOf<Long, Crocodile>()
        val newLogs = mutableMapOf<Long, Log>()

        //각 강의 spawnManager 의 spawn 시간 갱신하고, spawn 시간이면 새 객체 추가
        spawnManagers.forEach{ eachRiver ->
            eachRiver.currentTimeIncrease(updatePeriod)
            if(eachRiver.isItTimeForNewFloatingObject()){
                eachRiver.resetCurrentTime()
                addEachFloatingObject(eachRiver, newCrocodiles, newLogs)
            }
        }
        return FloatingObjectTables(newCrocodiles, newLogs)
    }

    /* FloatingObject 타입을 무작위로 골라서 타입에 맞는 테이블에 추가하는 함수 */
    private fun addEachFloatingObject(spawnManager: FloatObjectSpawnManager, newCrocodiles: MutableMap<Long, Crocodile>, newLogs: MutableMap<Long, Log>){
        val type = RandomNumber.get(typeCrocodile, typeLog)

        //타입에 따라 악어 혹은 통나무 생성
        if(type == typeCrocodile)
            newCrocodiles[loadObjectId()] = spawnManager.spawnCrocodile(loadFloatingObjectSize())
        else
            newLogs[loadObjectId()] = spawnManager.spawnLog(loadFloatingObjectSize())
    }


    //------------------------------------------------
    //함수 영역 (주기적 갱신 - floatingObject, floating frog 이동)
    //

    /* 떠 있는 객체들의 위치 이동을 하는 함수 */
    fun moveFloatingObjects(){
        //강에 떠 있는 경우 개구리의 이동
        floatingFrogMoveUpdate()

        //악어와 통나무의 이동
        crocodileTable.forEach{ it.value.positionUpdate() }
        logTable.forEach{ it.value.positionUpdate()}
    }

    /* 개구리가 floating 상태일 때 위치를 갱신하는 함수 */
    private fun floatingFrogMoveUpdate(){
        //floating 상태가 아니면 함수 종료
        if(!frog.isFloating)
            return

        //floating 상태면 움직임 갱신
        frog.floatingFrogMove()

        //떠다니다가 화면 밖으로 나가면 개구리 죽음 처리
        if(!areTheyOverlapped(frog.getPos(), frogSize, displayPosition, displaySize))
            frog.frogDead()
    }


    //------------------------------------------------
    //함수 영역 (주기적 갱신 - floatingObject 제거)
    //

    /* 떠 있는 객체들을 제거하는 함수 (제거한 객체 리스트 반환) */
    fun removeFloatingObjects(): FloatingObjectIds {
        //제거할 객체들 선별
        val removingCrocodiles = eachFloatingObjectRemoveUpdate(crocodileTable)
        val removingLogs = eachFloatingObjectRemoveUpdate(logTable)

        //실제 테이블에서 제거
        removingCrocodiles.forEach{ crocodileTable.remove(it) }
        removingLogs.forEach{ logTable.remove(it) }

        return FloatingObjectIds(removingCrocodiles, removingLogs)
    }

    /* FloatingObject 객체들을 제거하는 내부 함수 */
    private fun eachFloatingObjectRemoveUpdate(floatingObjectTable: Map<Long, FloatingObject>): List<Long>{
        val removeIdCheckList = mutableListOf<Long>()

        //테이블 속 floatingObject 객체가 화면을 벗어났다면 삭제 리스트에 id 추가
        floatingObjectTable.forEach{ eachFloatingObject ->
            if (!areTheyOverlapped(eachFloatingObject.value.getPos(), eachFloatingObject.value.size, displayPosition, displaySize))
                removeIdCheckList.add(eachFloatingObject.key)
        }

        return removeIdCheckList.toList()
    }


    //------------------------------------------------
    //함수 영역 (영역 혹은 객체끼리 겹치는지 확인하는 함수 - 객체 삭제, 개구리 죽음에 사용)
    //

    /* 두 개체가 서로 겹치는지 확인하는 함수 */
    private fun areTheyOverlapped(position1: Position, size1: ObjectSize, position2: Position, size2: ObjectSize): Boolean {
        //각 개체의 우하단 꼭지점 좌표 계산 (범위 계산을 위해)
        val inXRange = Pair(position1.x, position1.x + size1.width)
        val inYRange = Pair(position1.y, position1.y + size1.height)
        val outXRange = Pair(position2.x, position2.x + size2.width)
        val outYRange = Pair(position2.y, position2.y + size2.height)

        //두 개체가 가로 범위에서 겹치는 부분이 있는가?
        if(!areTheyOverlappedInLine(inXRange, outXRange))
            return false

        //두 개체가 세로 범위에서 겹치는 부분이 있는가?
        if(!areTheyOverlappedInLine(inYRange, outYRange))
            return false

        return true
    }

    /* 면이 아닌 선 범위에서 겹치는지 확인하는 함수 (areTheyOverlapped 부속 함수) */
    private fun areTheyOverlappedInLine(inRange:Pair<Float, Float>, outRange: Pair<Float, Float>): Boolean{
        if(inRange.first in outRange.first..outRange.second ||
                inRange.second in outRange.first..outRange.second)
            return true
        return false
    }
}