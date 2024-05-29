package com.example.checklisttest.cashtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checklisttest.CashbookAdapter
import com.example.checklisttest.CashbookDB
import com.example.checklisttest.CashbookData
import com.example.checklisttest.R
import com.example.checklisttest.databinding.ActivityCostCalcBinding
import com.example.checklisttest.databinding.ActivityItemTestBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CostCalcActivity : AppCompatActivity(), CashbookAdapter.TotalCostListener {
    private lateinit var binding: ActivityCostCalcBinding
    private lateinit var todoTitle: String
    val dbTool = CashbookDB()
    private lateinit var todoadapter: CashbookAdapter
    val itemList = ArrayList<CashbookData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCostCalcBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val listTitle = intent.getStringExtra("titleName")
        todoTitle = listTitle!!

        val rv_todolist = findViewById<RecyclerView>(R.id.rvTodoList)
        //갱신
        todoadapter = CashbookAdapter(itemList, todoTitle, this)
        todoadapter.notifyDataSetChanged()
        //어댑터
        rv_todolist.adapter = todoadapter
        rv_todolist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        //추가버튼
        binding.btnAdd.setOnClickListener {
            val title = binding.editContext.text.toString()
            val cost = binding.editPrice.text.toString().toInt()

            if(title.isNotEmpty()&&cost!=null) {
                dbTool.AddTodo(todoTitle!!, title!!, cost!!, false)
                Toast.makeText(this, "추가되었습니다.", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        //체크박스 토글기능
        todoadapter.setItemCheckBoxClickListener(object: CashbookAdapter.ItemCheckBoxClickListener{
            override fun onClick(view: View, position: Int, itemid: CashbookData) {
                CoroutineScope(Dispatchers.IO).launch {
                    val currentItem = itemList[position]
                    currentItem.isChecked = !currentItem.isChecked!!
                    dbTool.UpdateChecked(todoTitle!!, currentItem.isChecked!!, currentItem.itemName!!)
                }
            }
        })
    }
    // TotalCostListener 인터페이스의 메서드 구현
    override fun onTotalCostUpdated(totalCost: Int) {
        // totalCost 값을 받아와서 UI 업데이트 등의 작업 수행
        binding.totalSum.setText(totalCost.toString())
    }
}