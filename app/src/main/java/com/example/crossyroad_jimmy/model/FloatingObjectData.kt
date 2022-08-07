package com.example.crossyroad_jimmy.model

import com.example.crossyroad_jimmy.model.floatingObject.Crocodile
import com.example.crossyroad_jimmy.model.floatingObject.FloatingObject
import com.example.crossyroad_jimmy.model.floatingObject.Log

data class FloatingObjectData (
    val crocodiles: List<FloatingObject>,
    val logs: List<FloatingObject>
        )