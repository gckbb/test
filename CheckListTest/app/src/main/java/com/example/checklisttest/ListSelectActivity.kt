package com.example.checklisttest

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.checklisttest.databinding.ActivityListSelectBinding
import java.text.SimpleDateFormat

class ListSelectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListSelectBinding
    private var sNum = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListSelectBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val type = intent.getStringExtra("type")

        //라디오 버튼
        binding.rbGroup.setOnCheckedChangeListener{ group, checkedID ->
            when(checkedID){
                R.id.rb_1 -> {
                    Toast.makeText(applicationContext, "1선택", Toast.LENGTH_SHORT).show();
                    sNum = 1
                }
                R.id.rb_2 -> {
                    Toast.makeText(applicationContext, "2선택", Toast.LENGTH_SHORT).show();
                    sNum = 2
                }
                R.id.rb_3 -> {
                    Toast.makeText(applicationContext, "3선택", Toast.LENGTH_SHORT).show();
                    sNum = 3
                }
            }
        }

        //선택 완료
        binding.lsBtn.setOnClickListener {

            //체크리스트 목록 이름, 생성 일자, 분류 초기 설정
            val listTitle = binding.etTitle.text.toString()
            val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm").format(System.currentTimeMillis())
            if(type.equals("ADD")) {
                if(listTitle.isNotEmpty()) {
                    val checklist = CheckListData(listTitle, currentDate)
                    val intent = Intent().apply{
                        putExtra("listTitle", listTitle)
                        putExtra("currentDate", currentDate)
                        putExtra("sNum", sNum)
                    }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }

    }
}