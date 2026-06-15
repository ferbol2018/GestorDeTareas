package com.example.gestordetareas

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// Implementamos OnMapReadyCallback para trabajar con Google Maps
class FormActivity : AppCompatActivity(),
    OnMapReadyCallback {

    // Variable que controlará el mapa
    private lateinit var mMap: GoogleMap

    // Componente visual del mapa
    private lateinit var mapView: MapView

    // TextView donde se mostrará la ubicación seleccionada
    private lateinit var tvLocation: TextView

    // Variables para guardar las coordenadas seleccionadas
    private var selectedLatitude = 0.0

    private var selectedLongitude = 0.0

    // Instancia de autenticación Firebase
    private val auth = FirebaseAuth.getInstance()

    // Instancia de Firebase Realtime Database
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_form)

        // Referencias a los componentes visuales
        val etTaskName =
            findViewById<EditText>(R.id.etTaskName)

        val etTaskDescription =
            findViewById<EditText>(R.id.etTaskDescription)

        val btnSaveTask =
            findViewById<Button>(R.id.btnSaveTask)

        val btnBack =
            findViewById<Button>(R.id.btnBack)

        tvLocation =
            findViewById(R.id.tvLocation)

        mapView =
            findViewById(R.id.mapView)

        // Inicializa el mapa
        mapView.onCreate(savedInstanceState)

        // Carga el mapa de forma asíncrona
        mapView.getMapAsync(this)

        // Botón para guardar la tarea
        btnSaveTask.setOnClickListener {

            // Obtener el nombre y la descripción ingresados
            val name =
                etTaskName.text.toString().trim()

            val description =
                etTaskDescription.text.toString().trim()

            // Validar que el nombre no esté vacío
            if (name.isEmpty()) {

                etTaskName.error =
                    "Ingrese el nombre"

                return@setOnClickListener
            }

            // Obtener el ID del usuario autenticado
            val userId =
                auth.currentUser?.uid

            // Verificar que exista una sesión iniciada
            if (userId == null) {

                Toast.makeText(

                    this,

                    "Debe iniciar sesión",

                    Toast.LENGTH_SHORT

                ).show()

                return@setOnClickListener
            }

            // Crear un identificador único para la tarea
            val taskId =

                database.reference

                    .child("users")

                    .child(userId)

                    .child("tasks")

                    .push()

                    .key

            if (taskId != null) {

                // Crear el objeto tarea incluyendo la ubicación
                val task = Task(

                    name,

                    description,

                    selectedLatitude,

                    selectedLongitude

                )

                // Guardar la tarea en Firebase
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

                // Regresar a la pantalla anterior
                finish()
            }
        }

        // Botón volver
        btnBack.setOnClickListener {

            finish()
        }
    }

    // Se ejecuta cuando Google Maps termina de cargarse
    override fun onMapReady(
        googleMap: GoogleMap
    ) {

        mMap = googleMap

        // Habilitar botones + y -
        mMap.uiSettings.isZoomControlsEnabled = true

        // Habilitar zoom con los dedos
        mMap.uiSettings.isZoomGesturesEnabled = true

        // Permitir mover el mapa
        mMap.uiSettings.isScrollGesturesEnabled = true

        // Permitir rotar el mapa
        mMap.uiSettings.isRotateGesturesEnabled = true

        // Permitir inclinar el mapa
        mMap.uiSettings.isTiltGesturesEnabled = true

        // Mostrar el botón "Mi ubicación"
        mMap.uiSettings.isMyLocationButtonEnabled = true

        // Obtener la ubicación actual del usuario
        getCurrentLocation()

        // Detectar cuando el usuario toca el mapa
        mMap.setOnMapClickListener {

            // Eliminar marcadores anteriores
            mMap.clear()

            // Guardar las coordenadas seleccionadas
            selectedLatitude =
                it.latitude

            selectedLongitude =
                it.longitude

            // Agregar un marcador
            mMap.addMarker(

                MarkerOptions()

                    .position(it)

                    .title(
                        "Ubicación seleccionada"
                    )
            )

            // Mostrar las coordenadas en pantalla
            tvLocation.text =

                "📍 ${it.latitude}, ${it.longitude}"
        }
    }

    // Obtiene la ubicación actual del dispositivo
    private fun getCurrentLocation() {

        // Verificar permisos de ubicación
        if (

            ActivityCompat.checkSelfPermission(

                this,

                Manifest.permission.ACCESS_FINE_LOCATION

            )

            != PackageManager.PERMISSION_GRANTED

        ) {

            // Solicitar permisos al usuario
            ActivityCompat.requestPermissions(

                this,

                arrayOf(

                    Manifest.permission.ACCESS_FINE_LOCATION

                ),

                100

            )

            return
        }

        // Cliente que obtiene la ubicación del dispositivo
        val fusedLocationClient =

            LocationServices

                .getFusedLocationProviderClient(
                    this
                )

        fusedLocationClient.lastLocation

            .addOnSuccessListener {

                it?.let { location ->

                    // Crear coordenadas
                    val position =

                        LatLng(

                            location.latitude,

                            location.longitude

                        )

                    // Guardar las coordenadas
                    selectedLatitude =
                        location.latitude

                    selectedLongitude =
                        location.longitude

                    // Agregar marcador en la ubicación actual
                    mMap.addMarker(

                        MarkerOptions()

                            .position(position)

                            .title(
                                "Mi ubicación"
                            )
                    )

                    // Centrar el mapa en la ubicación actual
                    mMap.moveCamera(

                        CameraUpdateFactory

                            .newLatLngZoom(

                                position,

                                16f

                            )
                    )

                    // Activar el punto azul de Google Maps
                    mMap.isMyLocationEnabled = true

                    // Mostrar mensaje en pantalla
                    tvLocation.text =

                        "📍 Ubicación actual seleccionada"
                }
            }
    }

    // Ciclo de vida del MapView
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}