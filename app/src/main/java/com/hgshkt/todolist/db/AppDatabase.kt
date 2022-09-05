package com.hgshkt.todolist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hgshkt.todolist.model.Item

@Database(entities = [Item::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getItemDao(): ItemDao
}