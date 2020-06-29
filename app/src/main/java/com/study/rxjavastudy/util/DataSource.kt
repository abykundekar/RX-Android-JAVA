package com.study.rxjavastudy.util

import java.util.*

object DataSource {
    fun getListOfTask() : List<Task> {
        val tasks = LinkedList<Task>()
        tasks.add(Task("Wake Up Early", false, 1))
        tasks.add(Task("Do Some exercize", false, 2))
        tasks.add(Task("Do the Breakfast Fast ;)", true, 3))
        tasks.add(Task("Start the IT Work", true, 4))
        tasks.add(Task("Take some rest", true, 4))

        return tasks
    }
}