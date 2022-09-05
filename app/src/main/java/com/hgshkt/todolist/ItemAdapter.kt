package com.hgshkt.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hgshkt.todolist.MainActivity.Companion.db
import com.hgshkt.todolist.db.ItemDao
import com.hgshkt.todolist.model.Item


class ItemAdapter(private val context: Context, private val items: List<Item>,
    private val dao: ItemDao)
    : RecyclerView.Adapter<ItemAdapter.ImageViewHolder>() {


    companion object {
        // current item which is being edited
        var editPosition: Int? = null

        fun saveEdited() {
            val lastEditedItem = MainActivity.recyclerView.layoutManager!!.findViewByPosition(editPosition!!)
            val editTitle = lastEditedItem!!.findViewById<EditText>(R.id.editTitle)
            val itemTitle = lastEditedItem.findViewById<TextView>(R.id.itemTitle)
            val saveButton = lastEditedItem.findViewById<ImageView>(R.id.saveButton)
            val editButton = lastEditedItem.findViewById<ImageView>(R.id.editButton)

            itemTitle.text = editTitle.text
            editTitle.visibility = View.INVISIBLE
            itemTitle.visibility = View.VISIBLE

            saveButton.visibility = View.INVISIBLE
            editButton.visibility = View.VISIBLE

            db.getItemDao().updateItem(Item(
                editTitle.text.toString()
            ))
            editPosition = null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentItem = items[position]
        holder.title.text = currentItem.title
        loadTick(currentItem.complete, holder.tick)

        holder.saveButton.setOnClickListener {
            holder.title.text = holder.edit.text
            holder.edit.visibility = View.INVISIBLE
            holder.title.visibility = View.VISIBLE

            holder.saveButton.visibility = View.INVISIBLE
            holder.editButton.visibility = View.VISIBLE

            currentItem.title = holder.edit.text.toString()

            dao.updateItem(currentItem)

            editPosition = null
        }

        holder.editButton.setOnClickListener {
            if (editPosition != null) {
                saveEdited()
            } else {
                holder.edit.setText(holder.title.text)
                holder.title.visibility = View.INVISIBLE
                holder.edit.visibility = View.VISIBLE

                holder.editButton.visibility = View.INVISIBLE
                holder.saveButton.visibility = View.VISIBLE

                editPosition = position
            }
        }

        holder.deleteButton.setOnClickListener {
            if (editPosition != null) {
                saveEdited()
            } else {
                dao.delete(currentItem)
                (context as MainActivity).update()
            }
        }

        holder.tick.setOnClickListener {
            if (editPosition != null) {
                saveEdited()
            } else {
                currentItem.complete = !currentItem.complete
                loadTick(currentItem.complete, holder.tick)
                dao.updateItem(currentItem)
                (context as MainActivity).update()
            }
        }

        holder.title.setOnClickListener {
            //  if user has not saved the changes in previous item, save will happen automatically
            if (editPosition != null) {
                saveEdited()
            } else {

                holder.edit.setText(holder.title.text)
                holder.title.visibility = View.INVISIBLE
                holder.edit.visibility = View.VISIBLE

                holder.editButton.visibility = View.INVISIBLE
                holder.saveButton.visibility = View.VISIBLE

                editPosition = position
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun loadTick(complete: Boolean, view: ImageView) {
        Glide
            .with(context)
            .load(
                if (complete)
                    android.R.drawable.checkbox_on_background
                else
                    android.R.drawable.checkbox_off_background
            )
            .into(view)
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tick: ImageView
        var title: TextView
        var edit: EditText
        var saveButton: ImageView
        var editButton: ImageView
        var deleteButton: ImageView

        init {
            tick = itemView.findViewById(R.id.tick)
            title = itemView.findViewById(R.id.itemTitle)
            edit = itemView.findViewById(R.id.editTitle)
            saveButton = itemView.findViewById(R.id.saveButton)
            deleteButton = itemView.findViewById(R.id.deleteButton)
            editButton = itemView.findViewById(R.id.editButton)
        }
    }
}