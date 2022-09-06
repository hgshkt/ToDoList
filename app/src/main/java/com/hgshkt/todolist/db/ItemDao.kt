package com.hgshkt.todolist.db

import androidx.room.*
import com.hgshkt.todolist.model.Item

@Dao
interface ItemDao {

    @Insert
    fun insert(item: Item)

    @Delete
    fun delete(item: Item)

    @Update
    fun updateItem(item: Item)

    @Query("SELECT * FROM item")
    fun getAllItems(): List<Item>

    @Query("SELECT * FROM item WHERE id LIKE :id")
    fun getItemById(id: Int): Item

    @Query("SELECT * FROM item WHERE complete LIKE 1")
    fun getCompleted(): List<Item>

    @Query("SELECT * FROM item WHERE complete LIKE 0")
    fun getUncompleted(): List<Item>
}