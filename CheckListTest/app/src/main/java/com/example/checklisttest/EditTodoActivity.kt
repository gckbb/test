package com.example.checklisttest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.checklisttest.databinding.ActivityCheckListBinding
import com.example.checklisttest.databinding.ActivityEditTodoBinding
import com.example.checklisttest.databinding.ActivityMainBinding
import java.text.SimpleDateFormat

class EditTodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTodoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTodoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //세부목록 추가 시 넘어오는 화면
        val type = intent.getStringExtra("type")
        if(type.equals("ADD")){
            binding.btnSave.text = "추가하기"
        }else if(type.equals("EDIT")){
            val title = intent.getStringExtra("title")
            val content = intent.getStringExtra("content")

            binding.etTodoTitle.setText(title)
            binding.etTodoContent.setText(content)
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTodoTitle.text.toString()
            val content = binding.etTodoContent.text.toString()

            //todolist 추가 시
            if(type.equals("ADD")){
                if(title.isNotEmpty() && content.isNotEmpty()){
                    val todo = TodoListData(title, content, false)
                    val intent = Intent().apply{
                        putExtra("title", title)
                        putExtra("content", content)
                        putExtra("flag", 0)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }else if(type.equals("EDIT")){//todolist 수정 시
                if(title.isNotEmpty() && content.isNotEmpty()){
                    val intent = Intent().apply{
                        putExtra("title", title)
                        putExtra("content", content)
                        putExtra("flag", 0)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }

    }
}