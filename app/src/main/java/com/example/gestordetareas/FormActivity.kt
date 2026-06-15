package com.example.gestordetareas

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth

class FormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form)

        val etTaskName = findViewById<EditText>(R.id.etTaskName)
        val etTaskDescription = findViewById<EditText>(R.id.etTaskDescription)
        val btnSaveTask = findViewById<Button>(R.id.btnSaveTask)
        val btnBack = findViewById<Button>(R.id.btnBack)

        val auth = FirebaseAuth.getInstance()

        val database = FirebaseDatabase.getInstance()

        btnSaveTask.setOnClickListener {

            val name = etTaskName.text.toString().trim()

            val description =
                etTaskDescription.text.toString().trim()

            if (name.isEmpty()) {

                etTaskName.error =
                    "Ingrese el nombre de la tarea"

                return@setOnClickListener
            }

            val userId =
                auth.currentUser?.uid

            if (userId == null) {

                Toast.makeText(
                    this,
                    "Debe iniciar sesión",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val taskId =
                database.reference
                    .child("users")
                    .child(userId)
                    .child("tasks")
                    .push()
                    .key

            if (taskId != null) {

                val task = Task(
                    name,
                    description
                )

                database.reference
                    .child("users")
                    .child(userId)
                    .child("tasks")
                    .child(taskId)
                    .setValue(task)

                Toast.makeText(
                    this,
                    "Tarea guardada",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}