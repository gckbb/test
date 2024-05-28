package com.example.checklisttest

import android.app.Activity
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TodoAdapter(private var itemList: ArrayList<TodoListData>, private val listTitle: String) :
    RecyclerView.Adapter<TodoAdapter.TodoListViewHolder>() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = database.reference.child("checklist").child(listTitle).child("todo-list")
    val dbTool = CheckListDB()
    val listName = listTitle

    init {
        // Firebase Realtime Database에서 데이터를 가져와서 itemList에 추가
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                for (itemSnapshot in snapshot.children) {
                    val todoTitle =
                        itemSnapshot.child("todoTitle").getValue(String::class.java)
                    val todoContent =
                        itemSnapshot.child("todoContent").getValue(String::class.java)
                    val checked =
                        itemSnapshot.child("checked").getValue(Boolean::class.java)
                    todoTitle?.let { title ->
                        todoContent?.let { content ->
                            checked?.let { istodocheck ->
                                itemList.add(TodoListData(title, content, istodocheck))
                            }
                        }
                    }
                }
                notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터 읽기 실패 시 처리
            }
        })
    }

    inner class TodoListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var todoname = itemView.findViewById<TextView>(R.id.tvTodoItem)
        var todocontent = itemView.findViewById<TextView>(R.id.tvContent)
        var todochecked = itemView.findViewById<CheckBox>(R.id.cbCheck)

        fun onBind(data: TodoListData) {
            todoname.text = data.todoTitle
            todocontent.text = data.todoContent
            todochecked.isChecked = data.isChecked!!

            if (data.isChecked!!) {
                todoname.paintFlags =
                    todoname.paintFlags or STRIKE_THRU_TEXT_FLAG
            } else {
                todoname.paintFlags =
                    todoname.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            }
            todochecked.setOnClickListener {
                itemCheckBoxClickListener.onClick(itemView, layoutPosition, itemList[layoutPosition])

            }

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener?.onItemClick(
                        itemView,
                        position,
                        itemList[position]
                    )
                }
            }

            //리스트 삭제 시
            itemView.findViewById<Button>(R.id.btn_delete).setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val currentItem = itemList[position]
                    // 가져온 데이터를 사용하여 deleteList 함수를 호출합니다.
                    dbTool.DeleteTodo(listName ,currentItem.todoTitle!!)
                }
            }

        }
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int, todoName: String)
        fun onItemClick(view: View, position: Int, item: TodoListData)
    }
    private var itemClickListener: ItemClickListener? = null

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo_list, parent, false)
        return TodoListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    fun update(newList: ArrayList<TodoListData>) {
        itemList = newList
        notifyDataSetChanged()
    }

    interface ItemCheckBoxClickListener {
        fun onClick(view: View, position: Int, itemid: TodoListData)
    }
    private lateinit var itemCheckBoxClickListener: ItemCheckBoxClickListener

    fun setItemCheckBoxClickListener(itemCheckBoxClickListener: ItemCheckBoxClickListener) {
        this.itemCheckBoxClickListener = itemCheckBoxClickListener
    }

}
