package com.hgshkt.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hgshkt.todolist.db.AppDatabase
import com.hgshkt.todolist.model.Item


class ItemAdapter(private val context: Context, private val items: List<Item>,
    private val db: AppDatabase)
    : RecyclerView.Adapter<ItemAdapter.ImageViewHolder>() {

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
            holder.completeButton.visibility = View.VISIBLE

            currentItem.title = holder.edit.text.toString()

            db.getItemDao().updateItem(currentItem)
        }

        holder.itemLayout.setOnLongClickListener {
            // TODO: delete
            true
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
        var saveButton: Button
        var completeButton: Button
        var itemLayout: ConstraintLayout

        init {
            tick = itemView.findViewById(R.id.tick)
            title = itemView.findViewById(R.id.itemTitle)
            edit = itemView.findViewById(R.id.editTitle)
            saveButton = itemView.findViewById(R.id.saveButton)
            completeButton = itemView.findViewById(R.id.completeButton)
            itemLayout = itemView.findViewById(R.id.itemLayout)

            title.setOnClickListener {
                edit.setText(title.text)
                title.visibility = View.INVISIBLE
                edit.visibility = View.VISIBLE

                completeButton.visibility = View.INVISIBLE
                saveButton.visibility = View.VISIBLE
            }
        }
    }
}