package com.example.crossyroad_jimmy.model

import com.example.crossyroad_jimmy.model.floatingObject.Crocodile
import com.example.crossyroad_jimmy.model.floatingObject.Log

data class FloatingObjectData (
    val crocodiles: List<Crocodile>,
    val logs: List<Log>
        )