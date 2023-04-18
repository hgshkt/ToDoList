package com.hgshkt.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hgshkt.todolist.MainActivity.Companion.recyclerView
import com.hgshkt.todolist.db.ItemDao
import com.hgshkt.todolist.model.Item


class ItemAdapter(private val context: Context, private val items: List<Item>,
    private val dao: ItemDao)
    : RecyclerView.Adapter<ItemAdapter.ImageViewHolder>() {

    var editPosition: Int? = null
    var lastView: View? = null

    fun saveEdited() {
        var holder = recyclerView.findViewHolderForAdapterPosition(editPosition!!) as ImageViewHolder

        setVisibility(holder, false)

        MainActivity.dao.updateItem(Item(
            holder.edit.text.toString()
        ))
        editPosition = null
    }

    fun setVisibility(holder:  ItemAdapter.ImageViewHolder, toEdit: Boolean) {
        when (toEdit) {
            true -> {
                holder.edit.setText(holder.title.text)
                holder.title.visibility = View.INVISIBLE
                holder.edit.visibility = View.VISIBLE

                holder.editButton.visibility = View.INVISIBLE
                holder.saveButton.visibility = View.VISIBLE
            }
            false -> {
                holder.title.text = holder.edit.text
                holder.edit.visibility = View.INVISIBLE
                holder.title.visibility = View.VISIBLE

                holder.saveButton.visibility = View.INVISIBLE
                holder.editButton.visibility = View.VISIBLE
            }
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
            setVisibility(holder, false)

            currentItem.title = holder.edit.text.toString()

            dao.updateItem(currentItem)

            Toast.makeText(context, "note is saved", Toast.LENGTH_SHORT).show()

            editPosition = null
        }

        holder.editButton.setOnClickListener {
            if (editPosition != null) {
                saveEdited()
            } else {
                setVisibility(holder, true)
                editPosition = position
            }
        }

        holder.deleteButton.setOnClickListener {
            if (editPosition != null) {
                saveEdited()
            } else {
                Toast.makeText(context, "note is deleted", Toast.LENGTH_SHORT).show()
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