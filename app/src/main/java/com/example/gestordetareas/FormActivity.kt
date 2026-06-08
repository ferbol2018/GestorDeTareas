package com.example.gestordetareas

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class FormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form)

        val etTaskName = findViewById<EditText>(R.id.etTaskName)
        val etTaskDescription = findViewById<EditText>(R.id.etTaskDescription)
        val btnSaveTask = findViewById<Button>(R.id.btnSaveTask)
        val btnBack = findViewById<Button>(R.id.btnBack)

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("listTasks", Context.MODE_PRIVATE)

        btnSaveTask.setOnClickListener {

            val name = etTaskName.text.toString().trim()
            val description = etTaskDescription.text.toString().trim()

            if (name.isEmpty()) {
                etTaskName.error = "Ingrese el nombre de la tarea"
                return@setOnClickListener
            }

            val tasks =
                sharedPreferences.getStringSet("tasks", mutableSetOf())
                    ?.toMutableSet() ?: mutableSetOf()

            tasks.add("$name|$description")

            sharedPreferences.edit()
                .putStringSet("tasks", tasks)
                .apply()

            Toast.makeText(
                this,
                "Tarea guardada",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}