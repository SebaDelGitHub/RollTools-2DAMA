package com.example.rolltools

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AgregarPartida : AppCompatActivity() {

    private lateinit var etTitulo: EditText
    private lateinit var etJuego: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var tvFecha: TextView
    private lateinit var btnSeleccionarFecha: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnSeleccionarImagen: Button
    private lateinit var btnVolver: Button
    private lateinit var ivImagenSeleccionada: ImageView

   private var idUsuario :Int = 0

    private var fechaSeleccionada: String? = null
    private var imagenSeleccionada: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_partida)

        val bundle = intent.extras
         idUsuario = bundle?.getInt("idUsuario")!!


        etTitulo = findViewById(R.id.etTitulo)
        etJuego = findViewById(R.id.etJuego)
        etDescripcion = findViewById(R.id.etDescripcion)
        tvFecha = findViewById(R.id.tvFecha)
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen)
        btnVolver = findViewById(R.id.btnVolver)
        ivImagenSeleccionada = findViewById(R.id.ivImagenSeleccionada)

        btnSeleccionarFecha.setOnClickListener { mostrarDatePicker() }
        btnGuardar.setOnClickListener { guardarPartida() }
        btnSeleccionarImagen.setOnClickListener { seleccionarImagen() }
        btnVolver.setOnClickListener { finish() }
    }

    private fun mostrarDatePicker() {
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        //datepicker
        val datePickerDialog = DatePickerDialog(
            this, { _, anio, mes, dia ->
                val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val fecha = Calendar.getInstance()
                fecha.set(anio, mes, dia)
                fechaSeleccionada = formatoFecha.format(fecha.time)
                tvFecha.text = fechaSeleccionada
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            ivImagenSeleccionada.setImageBitmap(bitmap)
            imagenSeleccionada = bitmap
        }
    }

    private fun guardarPartida() {
        var titulo = etTitulo.text.toString()
        var juego = etJuego.text.toString()
        var descripcion = etDescripcion.text.toString()
        var fecha = fechaSeleccionada

        if (titulo.isNotEmpty() && juego.isNotEmpty() && fecha != null) {
            val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
            val db = admin.writableDatabase

            val values = ContentValues().apply {
                put("titulo", titulo)
                put("juego", juego)
                put("descripcion", descripcion)
                put("fecha", fecha)
                put("id_usuario", idUsuario)
                if (imagenSeleccionada != null) {
                    val outputStream = ByteArrayOutputStream()
                    imagenSeleccionada!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    put("imagen", outputStream.toByteArray())
                }
            }

            val resultado = db.insert("partidas", null, values)
            db.close()

            if (resultado != -1L) {
                Toast.makeText(this, "Partida agregada", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Completa todos los campos obligatorios, nombre, juego o fecha", Toast.LENGTH_SHORT).show()
        }
    }
}