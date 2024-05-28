package com.example.checklisttest

import java.io.Serializable

data class CheckListData(
    var listName: String? = null,
    var todoTimestamp: String? = null,
) : Serializable //intent로 넘기기 위해 상속

