package com.hgshkt.todolist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Item {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    lateinit var title: String
    var complete = false

    constructor() {}

    constructor(title: String) {
        this.title = title
    }
}