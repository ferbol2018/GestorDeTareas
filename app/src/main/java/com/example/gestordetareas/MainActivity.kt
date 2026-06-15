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

    // ListView donde se mostrarán las tareas
    private lateinit var listTasks: ListView

    // Referencia a Firebase Realtime Database
    private lateinit var database: DatabaseReference

    // Lista temporal que almacenará las tareas
    private val taskList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Referencia al ListView
        listTasks = findViewById(R.id.listTasks)

        // Botón para agregar una nueva tarea
        val btnAddTask =
            findViewById<Button>(R.id.btnAddTask)

        // Botón para cerrar la sesión
        val btnLogout =
            findViewById<ImageButton>(R.id.btnLogout)

        // Obtener el identificador único del usuario autenticado
        val userId =
            FirebaseAuth.getInstance()
                .currentUser
                ?.uid

        // Crear la ruta donde se almacenan las tareas del usuario
        if (userId != null) {

            database = FirebaseDatabase

                .getInstance()

                .getReference("users")

                .child(userId)

                .child("tasks")
        }

        // Abrir la pantalla para crear una nueva tarea
        btnAddTask.setOnClickListener {

            startActivity(

                Intent(

                    this,

                    FormActivity::class.java

                )
            )
        }

        // Evento del botón cerrar sesión
        btnLogout.setOnClickListener {

            AlertDialog.Builder(this)

                .setTitle("Cerrar sesión")

                .setMessage(
                    "¿Desea cerrar la sesión?"
                )

                .setPositiveButton("Sí") { _, _ ->

                    // Cerrar la sesión actual
                    FirebaseAuth

                        .getInstance()

                        .signOut()

                    Toast.makeText(

                        this,

                        "Sesión cerrada",

                        Toast.LENGTH_SHORT

                    ).show()

                    // Regresar a la pantalla de Login
                    startActivity(

                        Intent(

                            this,

                            Login::class.java

                        )
                    )

                    // Finalizar MainActivity
                    finish()
                }

                .setNegativeButton(
                    "No",
                    null
                )

                .show()
        }

        // Cargar las tareas guardadas
        loadTasks()
    }

    // Método encargado de leer las tareas desde Firebase
    private fun loadTasks() {

        database.addValueEventListener(

            object : ValueEventListener {

                // Se ejecuta cuando Firebase detecta cambios
                override fun onDataChange(

                    snapshot: DataSnapshot

                ) {

                    // Limpiar la lista para evitar duplicados
                    taskList.clear()

                    // Recorrer todas las tareas
                    for (data in snapshot.children) {

                        // Convertir los datos a un objeto Task
                        val task =

                            data.getValue(
                                Task::class.java
                            )

                        task?.let {

                            // Agregar la tarea a la lista
                            taskList.add(

                                "📌 ${it.name}\n${it.description}"

                            )
                        }
                    }

                    // Crear el adaptador del ListView
                    val adapter =

                        ArrayAdapter(

                            this@MainActivity,

                            android.R.layout.simple_list_item_1,

                            taskList
                        )

                    // Mostrar las tareas en pantalla
                    listTasks.adapter =

                        adapter
                }

                // Se ejecuta si ocurre un error en Firebase
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