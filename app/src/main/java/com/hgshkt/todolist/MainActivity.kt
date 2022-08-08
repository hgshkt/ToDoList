package com.hgshkt.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.hgshkt.todolist.db.AppDatabase
import com.hgshkt.todolist.model.Item

class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ItemAdapter
    lateinit var itemList: List<Item>

    lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = Room
            .databaseBuilder(applicationContext, AppDatabase::class.java, "itemDatabase")
            .allowMainThreadQueries()
            .build()

        itemList = mutableListOf()
        adapter = ItemAdapter(applicationContext, itemList, db)
        recyclerView.adapter = adapter

        (itemList as ArrayList).addAll(db.getItemDao().getAllItems())
    }

    override fun onResume() {
        super.onResume()

        recyclerView.setHasFixedSize(true)
        adapter = ItemAdapter(this@MainActivity, itemList, db)
        recyclerView.adapter = adapter
    }

    fun add(view: View) {
        val newItem = Item("NEW ITEM")
        db.getItemDao().insertAll(newItem)
        recyclerView.scrollToPosition(newItem.id)

        update()

        Toast.makeText(applicationContext, "add", Toast.LENGTH_LONG).show()
    }

    fun update() {
        (itemList as ArrayList).clear()
        var item: List<Item> = db.getItemDao().getAllItems()
        for (item in db.getItemDao().getAllItems() as MutableList) {
            (itemList as MutableList).add(item)
        }
        adapter = ItemAdapter(this@MainActivity, itemList, db)
        recyclerView.adapter = adapter
    }
}