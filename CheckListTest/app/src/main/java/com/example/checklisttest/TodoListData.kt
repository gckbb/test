package com.example.checklisttest

import java.io.Serializable

data class TodoListData(
    var todoTitle:String? = null,
    var todoContent:String? = null,
    var isChecked:Boolean? = false
) : Serializable
