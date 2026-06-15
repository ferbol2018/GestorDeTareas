package com.example.gestordetareas

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

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

        val btnLogout =
            findViewById<ImageButton>(R.id.btnLogout)

        btnAddTask.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    FormActivity::class.java
                )
            )
        }

        btnLogout.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Desea cerrar la sesión?")
                .setPositiveButton("Sí") { _, _ ->

                    FirebaseAuth.getInstance().signOut()

                    Toast.makeText(
                        this,
                        "Sesión cerrada",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(
                            this,
                            Login::class.java
                        )
                    )

                    finish()
                }

                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun loadTasks() {

        val savedTasks =
            sharedPreferences.getStringSet(
                "tasks",
                emptySet()
            ) ?: emptySet()

        val taskList = ArrayList<String>()

        for (task in savedTasks) {

            val data = task.split("|")

            val name =
                data.getOrElse(0) { "" }

            val description =
                data.getOrElse(1) { "" }

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