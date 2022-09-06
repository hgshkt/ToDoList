package com.hgshkt.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.hgshkt.todolist.ItemAdapter.Companion.editPosition
import com.hgshkt.todolist.ItemAdapter.Companion.saveEdited
import com.hgshkt.todolist.db.AppDatabase
import com.hgshkt.todolist.db.ItemDao
import com.hgshkt.todolist.model.Item
import com.hgshkt.todolist.service.AddItemService

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var recyclerView: RecyclerView
        lateinit var db: AppDatabase
        lateinit var dao: ItemDao

        lateinit var itemList: List<Item>
    }

    lateinit var adapter: ItemAdapter
    private var currentFilter = Filter.ALL

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
        dao = db.getItemDao()

        itemList = mutableListOf()
        adapter = ItemAdapter(applicationContext, itemList, dao)
        recyclerView.adapter = adapter

        (itemList as ArrayList).addAll(dao.getAllItems())
    }

    override fun onResume() {
        super.onResume()

        recyclerView.setHasFixedSize(true)
        adapter = ItemAdapter(this@MainActivity, itemList, dao)
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.custom_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        currentFilter = when (item.itemId) {
            R.id.show_all -> {
                Filter.ALL
            }
            R.id.show_completed -> {
                Filter.COMPLETED
            }
            R.id.show_uncompleted -> {
                Filter.UNCOMPLETED
            }
            else -> return super.onOptionsItemSelected(item)
        }
        update()
        return true
    }

    fun add(view: View) {
        if (editPosition != null)
            saveEdited()
        else {
            val newItem = Item("")
            dao.insert(newItem)
            recyclerView.scrollToPosition(newItem.id)

            update()

            startService(Intent(applicationContext, AddItemService::class.java))
        }
    }

    fun update() {
        (itemList as ArrayList).clear()

        var filteredList = when (currentFilter) {
            Filter.ALL -> dao.getAllItems()
            Filter.COMPLETED -> dao.getCompleted()
            Filter.UNCOMPLETED -> dao.getUncompleted()
        }

        for (item in filteredList as MutableList) {
            (itemList as MutableList).add(item)
        }

        adapter = ItemAdapter(this@MainActivity, itemList, dao)
        recyclerView.adapter = adapter
    }

    private enum class Filter {
        ALL, COMPLETED, UNCOMPLETED
    }
}