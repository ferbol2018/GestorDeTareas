package com.example.gestordetareas

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var listTasks: ListView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listTasks = findViewById(R.id.listTasks)

        sharedPreferences =
            getSharedPreferences("listTasks", Context.MODE_PRIVATE)

        val btnAddTask = findViewById<Button>(R.id.btnAddTask)

        btnAddTask.setOnClickListener {
            startActivity(
                Intent(this, FormActivity::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun loadTasks() {

        val savedTasks =
            sharedPreferences.getStringSet("tasks", emptySet())
                ?: emptySet()

        val taskList = ArrayList<String>()

        for (task in savedTasks) {

            val data = task.split("|")

            val name = data.getOrElse(0) { "" }
            val description = data.getOrElse(1) { "" }

            taskList.add(
                "📌 $name\n$description"
            )
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            taskList
        )

        listTasks.adapter = adapter
    }
}