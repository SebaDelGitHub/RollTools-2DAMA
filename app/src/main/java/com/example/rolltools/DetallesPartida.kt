package com.example.rolltools

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class DetallesPartida : AppCompatActivity() {

    private lateinit var etTitulo: EditText
    private lateinit var etJuego: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var tvFecha: TextView
    private lateinit var ivImagenPartida: ImageView
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnVolver: Button
    private var idPartida: Int = -1
    private var imagenPartida: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_partida)

        etTitulo = findViewById(R.id.etTitulo)
        etJuego = findViewById(R.id.etJuego)
        etDescripcion = findViewById(R.id.etDescripcion)
        tvFecha = findViewById(R.id.tvFecha)
        ivImagenPartida = findViewById(R.id.ivImagenPartida)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnVolver = findViewById(R.id.btnVolver)

        idPartida = intent.getIntExtra("idPartida", -1)

        cargarDetalles()

        btnGuardar.setOnClickListener { guardarCambios() }
        btnEliminar.setOnClickListener { eliminarPartida() }
        btnVolver.setOnClickListener { finish() }
    }

    private fun cargarDetalles() {
        val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
        val db = admin.readableDatabase
        val cursor = db.rawQuery("SELECT titulo, juego, descripcion, fecha, imagen FROM partidas WHERE id_partida = ?", arrayOf(idPartida.toString()))

        if (cursor.moveToFirst()) {
            etTitulo.setText(cursor.getString(0))
            etJuego.setText(cursor.getString(1))
            etDescripcion.setText(cursor.getString(2))
            tvFecha.text = cursor.getString(3)

            val imagenByteArray = cursor.getBlob(4)
            if (imagenByteArray != null) {
                val inputStream = ByteArrayInputStream(imagenByteArray)
                imagenPartida = BitmapFactory.decodeStream(inputStream)
                ivImagenPartida.setImageBitmap(imagenPartida)
            }
        }
        cursor.close()
        db.close()
    }

    private fun guardarCambios() {
        var titulo = etTitulo.text.toString()
        var juego = etJuego.text.toString()
        var descripcion = etDescripcion.text.toString()
        var fecha = tvFecha.text.toString()

        if (!(titulo.isEmpty()) && !(juego.isEmpty()) && !(descripcion.isEmpty()) && !(fecha.isEmpty())) {
            val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
            val db = admin.writableDatabase

            val values = ContentValues().apply {
                put("titulo", titulo)
                put("juego", juego)
                put("descripcion", descripcion)
                put("fecha", fecha)
                if (imagenPartida != null) {
                    val outputStream = ByteArrayOutputStream()
                    imagenPartida!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    put("imagen", outputStream.toByteArray())
                }
            }

            val result = db.update("partidas", values, "id_partida = ?", arrayOf(idPartida.toString()))
            db.close()

            if (result > 0) {
                Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al guardar los cambios", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarPartida() {
        val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
        val db = admin.writableDatabase

        val filasEliminadas = db.delete("partidas", "id_partida = ?", arrayOf(idPartida.toString()))
        db.close()

        if (filasEliminadas > 0) {
            Toast.makeText(this, "Partida eliminada", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al eliminar la partida", Toast.LENGTH_SHORT).show()
        }
    }
}