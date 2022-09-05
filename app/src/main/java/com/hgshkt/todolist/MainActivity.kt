package com.hgshkt.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.hgshkt.todolist.ItemAdapter.Companion.editPosition
import com.hgshkt.todolist.ItemAdapter.Companion.saveEdited
import com.hgshkt.todolist.db.AppDatabase
import com.hgshkt.todolist.model.Item

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var recyclerView: RecyclerView
        lateinit var db: AppDatabase

        lateinit var itemList: List<Item>
    }

    lateinit var adapter: ItemAdapter

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
        adapter = ItemAdapter(applicationContext, itemList, db.getItemDao())
        recyclerView.adapter = adapter

        (itemList as ArrayList).addAll(db.getItemDao().getAllItems())
    }

    override fun onResume() {
        super.onResume()

        recyclerView.setHasFixedSize(true)
        adapter = ItemAdapter(this@MainActivity, itemList, db.getItemDao())
        recyclerView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_all -> {
                update()
                true
            }
            R.id.show_completed -> {
                (itemList as ArrayList).clear()
                for (item in db.getItemDao().getCompleted() as MutableList) {
                    (itemList as MutableList).add(item)
                }
                adapter = ItemAdapter(this@MainActivity, itemList, db.getItemDao())
                recyclerView.adapter = adapter

                true
            }
            R.id.show_uncompleted -> {
                (itemList as ArrayList).clear()
                for (item in db.getItemDao().getUncompleted() as MutableList) {
                    (itemList as MutableList).add(item)
                }
                adapter = ItemAdapter(this@MainActivity, itemList, db.getItemDao())
                recyclerView.adapter = adapter

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun add(view: View) {
        saveEdited()

        val newItem = Item("NEW ITEM")
        db.getItemDao().insertAll(newItem)
        recyclerView.scrollToPosition(newItem.id)

        update()
    }

    fun update() {
        (itemList as ArrayList).clear()
        for (item in db.getItemDao().getAllItems() as MutableList) {
            (itemList as MutableList).add(item)
        }
        adapter = ItemAdapter(this@MainActivity, itemList, db.getItemDao())
        recyclerView.adapter = adapter
    }
}