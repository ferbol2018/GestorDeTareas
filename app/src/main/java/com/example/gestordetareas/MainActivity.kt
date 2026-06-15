package com.example.gestordetareas

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var listTasks: ListView

    private lateinit var database: DatabaseReference

    private val taskList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        listTasks = findViewById(R.id.listTasks)

        val btnAddTask =
            findViewById<Button>(R.id.btnAddTask)

        val btnLogout =
            findViewById<ImageButton>(R.id.btnLogout)

        val userId =
            FirebaseAuth.getInstance()
                .currentUser
                ?.uid

        if (userId != null) {

            database = FirebaseDatabase
                .getInstance()

                .getReference("users")

                .child(userId)

                .child("tasks")
        }

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

                .setMessage(
                    "¿Desea cerrar la sesión?"
                )

                .setPositiveButton("Sí") { _, _ ->

                    FirebaseAuth.getInstance()
                        .signOut()

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

                .setNegativeButton(
                    "No",
                    null
                )

                .show()
        }

        loadTasks()
    }

    private fun loadTasks() {

        database.addValueEventListener(

            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    taskList.clear()

                    for (data in snapshot.children) {

                        val task =
                            data.getValue(
                                Task::class.java
                            )

                        task?.let {

                            taskList.add(
                                "📌 ${it.name}\n${it.description}"
                            )
                        }
                    }

                    val adapter =
                        ArrayAdapter(

                            this@MainActivity,

                            android.R.layout.simple_list_item_1,

                            taskList
                        )

                    listTasks.adapter =
                        adapter
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {

                    Toast.makeText(

                        this@MainActivity,

                        error.message,

                        Toast.LENGTH_SHORT

                    ).show()
                }
            }
        )
    }
}