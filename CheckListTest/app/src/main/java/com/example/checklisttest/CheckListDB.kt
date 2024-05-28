package com.example.checklisttest

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CheckListDB {
    private val db = Firebase.database
    private val myRef = db.getReference("checklist")

    //체크리스트 최초 생성 시
    fun initCheckList(listName:String, currentDate:String){
        val checklist = CheckListData(listName, currentDate)
        myRef.child(listName).setValue(checklist)
    }

    //todo생성 시
    fun AddTodo(listName:String, todoTitle:String, todoContent:String, isChecked:Boolean){
        val todolist = TodoListData(todoTitle, todoContent, isChecked)
        myRef.child(listName).child("todo-list").child(todoTitle).setValue(todolist)
    }

    fun UpdateChecked(listName:String, isChecked:Boolean, todoTitle:String){
        myRef.child(listName).child("todo-list").child(todoTitle).child("checked").setValue(isChecked)
    }

    //목록 삭제 시
    fun DeleteList(listName:String){
        myRef.child(listName).removeValue()
    }

    //todolist 삭제 시
    fun DeleteTodo(listName: String, todoTitle: String){
        myRef.child(listName).child("todo-list").child(todoTitle).removeValue()
    }

    //산바다계곡
    fun sNum1(listName:String){
        AddTodo(listName,"겉옷챙기기", "산", false)
    }
    fun sNum2(listName:String){
        AddTodo(listName,"수영복챙기기", "바다", false)
    }
    fun sNum3(listName:String){
        AddTodo(listName,"음식챙기기", "계곡", false)
    }
}