package com.example.checklisttest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checklisttest.databinding.ActivityAfterSelectCashBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AfterSelectCashActivity : AppCompatActivity(), CashbookAdapter.TotalCostListener{
    private lateinit var binding: ActivityAfterSelectCashBinding
    private lateinit var todoadapter: CashbookAdapter
    val itemList = ArrayList<CashbookData>()
    private lateinit var todoTitle: String
    val dbTool = CashbookDB()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAfterSelectCashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val listTitle = intent.getStringExtra("titleName")
        todoTitle = listTitle!!

        binding.tvListTitle.text = listTitle
        val rv_todolist = findViewById<RecyclerView>(R.id.rvTodoList)
        //갱신
        todoadapter = CashbookAdapter(itemList, todoTitle, this)
        todoadapter.notifyDataSetChanged()
        //어댑터
        rv_todolist.adapter = todoadapter
        rv_todolist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        var totalCost = todoadapter.CalcTotalCost()
        //findViewById<TextView>(R.id.total_cost).text = totalCost.toString()
        binding.totalCost.text = totalCost.toString()


        // 추가 버튼 선택시
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, EditCashActivity::class.java).apply{
                putExtra("type", "ADD")
                putExtra("listTitle",listTitle)
            }
            requestActivity.launch(intent)
        }

        //이미 생성된 리스트 선택하면 해당 가계부 화면으로 가야함
        todoadapter.setItemClickListener(object: CashbookAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int, titleName: String) {
                Toast.makeText(this@AfterSelectCashActivity, "$titleName", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    val intent = Intent(this@AfterSelectCashActivity, AfterSelectCashActivity::class.java).apply{
                        putExtra("titleName", titleName)
                    }
                }
                requestActivity.launch(intent)
            }

            // 수정시
            override fun onItemClick(view: View, position: Int, item: CashbookData) {
                val intent = Intent(this@AfterSelectCashActivity, EditCashActivity::class.java).apply{
                    putExtra("title", item.itemName)
                    putExtra("content", item.itemCost)
                    putExtra("type", "EDIT")
                }
                startActivity(intent)
            }
        })

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
    // updateTotalCost 메서드 정의
    private fun updateTotalCost(totalCost: Int) {
        findViewById<TextView>(R.id.total_cost).text = totalCost.toString()
    }

    //Edit 화면으로 다녀온 이후
    private val requestActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            val data = it.data
            val title = data?.getStringExtra("title")
            val content = data?.getIntExtra("content", 0)

            when(it.data?.getIntExtra("flag", -1)){
                0 -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        dbTool.AddTodo(todoTitle!!, title!!, content!!.toInt(), false)
                    }
                    Toast.makeText(this, "추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onTotalCostUpdated(totalCost: Int) {
        // totalCost 값을 받아와서 UI 업데이트 등의 작업 수행
        binding.totalCost.text = "Total cost: ${totalCost}"
    }

}


