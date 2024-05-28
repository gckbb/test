package com.example.checklisttest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CAdapter(private var itemList: ArrayList<CheckListData>): RecyclerView.Adapter<CAdapter.CheckListViewHolder>() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = database.reference.child("checklist")
    val dbTool = CheckListDB()

    init {
        // Firebase Realtime Database에서 데이터를 가져와서 itemList에 추가
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                for (itemSnapshot in snapshot.children) {
                    val listName = itemSnapshot.child("listName").getValue(String::class.java)
                    val todoTimestamp =
                        itemSnapshot.child("todoTimestamp").getValue(String::class.java)
                    listName?.let { name ->
                        todoTimestamp?.let { timestamp ->
                            itemList.add(CheckListData(name, timestamp))
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

    inner class CheckListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var listName = itemView.findViewById<TextView>(R.id.tvTodoItem)
        var timestamp = itemView.findViewById<TextView>(R.id.tvTimeStamp)

        fun onBind(data: CheckListData){
            listName.text = data.listName
            timestamp.text = data.todoTimestamp

            itemView.setOnClickListener{
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(itemView, position, itemList[position])
                }
            }

            //리스트 삭제 시
            itemView.findViewById<Button>(R.id.delete_btn).setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val currentItem = itemList[position]
                    // 가져온 데이터를 사용하여 deleteList 함수를 호출합니다.
                    dbTool.DeleteList(currentItem.listName!!)
                }
            }
        }
    }

    interface ItemClickListener{
        fun onClick(view: View, position: Int, titleName:String)

        fun onItemClick(view: View, position: Int, item: CheckListData)
    }

    private var itemClickListener: ItemClickListener? = null

    fun setOnItemClickListener(listener: ItemClickListener){
        this.itemClickListener = listener
    }

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_check_list, parent, false)
        return CheckListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    override fun onBindViewHolder(holder: CheckListViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    fun update(newList: ArrayList<CheckListData>){
        itemList = newList
        notifyDataSetChanged()
    }

}