package com.hgshkt.todolist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.hgshkt.todolist.db.AppDatabase
import com.hgshkt.todolist.db.ItemDao
import com.hgshkt.todolist.model.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    companion object {
        lateinit var recyclerView: RecyclerView
        lateinit var db: AppDatabase
        lateinit var dao: ItemDao
    }

    private var currentFilter = Filter.ALL

    lateinit var itemList: List<Item>
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
            R.id.addButton -> {
                add()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
        update()
        return true
    }

    fun add() {
        if (adapter.editPosition != null)
            adapter.saveEdited()
        else {
            val newItem = Item("")
            dao.insert(newItem)
            update()
            recyclerView.scrollToPosition(adapter.itemCount - 1)

            Toast.makeText(applicationContext, "new note", Toast.LENGTH_SHORT).show()
        }
    }

    fun update() {
        (itemList as ArrayList).clear()

        var filteredList = currentFilter.filterOut()

        for (item in filteredList as MutableList) {
            (itemList as MutableList).add(item)
        }

        adapter = ItemAdapter(this@MainActivity, itemList, dao)
        recyclerView.adapter = adapter
    }

    private enum class Filter {
        ALL {
            override fun filterOut() = dao.getAllItems()
        },
        COMPLETED {
            override fun filterOut() = dao.getCompleted()
        },
        UNCOMPLETED {
            override fun filterOut() = dao.getUncompleted()
        };

        abstract fun filterOut(): List<Item>
    }
}