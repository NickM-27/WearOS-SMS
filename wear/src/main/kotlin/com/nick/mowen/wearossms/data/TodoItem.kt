package com.nick.mowen.wearossms.data

import java.util.*

class TodoItem private constructor(var text: String) {

    var done = false

    companion object {

        fun getList(list: Array<String>?): ArrayList<TodoItem> {
            val todoList = ArrayList<TodoItem>()

            if (list != null && list.isNotEmpty())
                list.mapTo(todoList) { TodoItem(it) }

            return todoList
        }
    }
}