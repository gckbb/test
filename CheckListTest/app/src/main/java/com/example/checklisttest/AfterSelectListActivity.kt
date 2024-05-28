package com.example.checklisttest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checklisttest.databinding.ActivityAfterSelectListBinding
import com.example.checklisttest.databinding.ActivityEditTodoBinding
import com.example.checklisttest.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AfterSelectListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAfterSelectListBinding
    private lateinit var todoadapter: TodoAdapter
    val itemList = ArrayList<TodoListData>()
    private lateinit var todoTitle: String
    val dbTool = CheckListDB()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAfterSelectListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val listTitle = intent.getStringExtra("titleName")
        todoTitle = listTitle!!

        binding.tvListTitle.text = listTitle
        val rv_todolist = findViewById<RecyclerView>(R.id.rvTodoList)
        //갱신
        todoadapter = TodoAdapter(itemList, todoTitle)
        todoadapter.notifyDataSetChanged()
        //어댑터
        rv_todolist.adapter = todoadapter
        rv_todolist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // 추가 버튼 선택시
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, EditTodoActivity::class.java).apply{
                putExtra("type", "ADD")
                putExtra("listTitle",listTitle)
            }
            requestActivity.launch(intent)
        }

        //이미 생성된 리스트 선택하면 해당 투두리스트 화면으로 가야함
        todoadapter.setItemClickListener(object: TodoAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int, titleName: String) {
                Toast.makeText(this@AfterSelectListActivity, "$titleName", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    val intent = Intent(this@AfterSelectListActivity, AfterSelectListActivity::class.java).apply{
                        putExtra("titleName", titleName)
                    }
                }
                requestActivity.launch(intent)
            }

            //todolist 수정시
            override fun onItemClick(view: View, position: Int, item: TodoListData) {
                val intent = Intent(this@AfterSelectListActivity, EditTodoActivity::class.java).apply{
                    putExtra("title", item.todoTitle)
                    putExtra("content", item.todoContent)
                    putExtra("type", "EDIT")
                }
                startActivity(intent)
            }
        })

        //체크박스 토글기능
        todoadapter.setItemCheckBoxClickListener(object: TodoAdapter.ItemCheckBoxClickListener{
            override fun onClick(view: View, position: Int, itemid: TodoListData) {
                CoroutineScope(Dispatchers.IO).launch {
                    val currentItem = itemList[position]
                    currentItem.isChecked = !currentItem.isChecked!!
                    dbTool.UpdateChecked(todoTitle!!, currentItem.isChecked!!, currentItem.todoTitle!!)
                }
            }
        })

    }

    //Edittodo 화면으로 다녀온 이후
    private val requestActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            val data = it.data
            val title = data?.getStringExtra("title")
            val content = data?.getStringExtra("content")

            when(it.data?.getIntExtra("flag", -1)){
                0 -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        dbTool.AddTodo(todoTitle!!, title!!, content!!, false)
                    }
                    Toast.makeText(this, "추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}

