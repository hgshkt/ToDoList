package com.hgshkt.todolist.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import com.hgshkt.todolist.ItemAdapter
import com.hgshkt.todolist.MainActivity
import com.hgshkt.todolist.R

class AddItemService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var newItemView =
            MainActivity.recyclerView.layoutManager!!.findViewByPosition(MainActivity.itemList.size - 1)!!

        MainActivity.recyclerView.layoutManager!!.findViewByPosition(MainActivity.itemList.size - 1)!!

        val editTitle = newItemView.findViewById<EditText>(R.id.editTitle)
        val itemTitle = newItemView.findViewById<TextView>(R.id.itemTitle)
        val saveButton = newItemView.findViewById<ImageView>(R.id.saveButton)
        val editButton = newItemView.findViewById<ImageView>(R.id.editButton)

        editTitle.setText(itemTitle.text)
        itemTitle.visibility = View.INVISIBLE
        editTitle.visibility = View.VISIBLE

        editButton.visibility = View.INVISIBLE
        saveButton.visibility = View.VISIBLE

        ItemAdapter.editPosition = MainActivity.itemList.size - 1

        return super.onStartCommand(intent, flags, startId)
    }
}