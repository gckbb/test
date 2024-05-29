package com.example.checklisttest

import android.app.Activity
import android.content.Context
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CashbookAdapter(private var itemList: ArrayList<CashbookData>, private val listTitle: String) :
    RecyclerView.Adapter<CashbookAdapter.CashbookViewHolder>() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = database.reference.child("cashbook").child(listTitle).child("cashbook-list")
    val dbTool = CashbookDB()
    val listName = listTitle
    var totalCost:Int = 0

    init {
        // Firebase Realtime Database에서 데이터를 가져와서 itemList에 추가
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                totalCost = 0

                for (itemSnapshot in snapshot.children) {
                    val todoTitle =
                        itemSnapshot.child("itemName").getValue(String::class.java)
                    val todoContent =
                        itemSnapshot.child("itemCost").getValue(Int::class.java)
                    if (todoContent != null) {
                        totalCost += todoContent
                    }
                    val checked =
                        itemSnapshot.child("checked").getValue(Boolean::class.java)
                    todoTitle?.let { title ->
                        todoContent?.let { content ->
                            checked?.let { istodocheck ->
                                itemList.add(CashbookData(title, content, istodocheck))
                            }
                        }
                    }
                }
                notifyDataSetChanged()
                CalcTotalCost()
            }

            override fun onCancelled(error: DatabaseError) {
                // 데이터 읽기 실패 시 처리
            }
        })
    }
    fun CalcTotalCost(): Int {
        return totalCost
    }


    inner class CashbookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var todoname = itemView.findViewById<TextView>(R.id.tvTodoItem)
        var todocontent = itemView.findViewById<TextView>(R.id.tvContent)
        var todochecked = itemView.findViewById<CheckBox>(R.id.cbCheck)
        var cost = itemView.findViewById<TextView>(R.id.total_cost)

        fun onBind(data: CashbookData) {
            todoname.text = data.itemName
            todocontent.text = data.itemCost.toString()
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
                    dbTool.DeleteTodo(listName ,currentItem.itemName!!)
                }
            }

        }

    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int, todoName: String)
        fun onItemClick(view: View, position: Int, item: CashbookData)
    }
    private var itemClickListener: ItemClickListener? = null

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashbookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo_list, parent, false)
        return CashbookViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    override fun onBindViewHolder(holder: CashbookViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    fun update(newList: ArrayList<CashbookData>) {
        itemList = newList
        notifyDataSetChanged()
    }

    interface ItemCheckBoxClickListener {
        fun onClick(view: View, position: Int, itemid: CashbookData)
    }
    private lateinit var itemCheckBoxClickListener: ItemCheckBoxClickListener

    fun setItemCheckBoxClickListener(itemCheckBoxClickListener: ItemCheckBoxClickListener) {
        this.itemCheckBoxClickListener = itemCheckBoxClickListener
    }

}
