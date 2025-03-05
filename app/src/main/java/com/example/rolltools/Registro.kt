package com.example.rolltools

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class Registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        var nombreUsuario = findViewById<EditText>(R.id.et1)
        val clave = findViewById<EditText>(R.id.password)
        val clave2 = findViewById<EditText>(R.id.passwordConfirm)

        val botonLogIn = findViewById<Button>(R.id.buttonLog)
        val botonSingIn = findViewById<Button>(R.id.buttonSing)

        var aviso = findViewById<TextView>(R.id.tv1)

        val backgroundGif: ImageView = findViewById(R.id.backgroundGif)
        Glide.with(this)
            .asGif()
            .load(R.drawable.orange)
            .into(backgroundGif)

        botonSingIn.setOnClickListener {

            val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
            val bd = admin.writableDatabase

            val nombreUsuarioTexto = nombreUsuario.text.toString()
            val claveTexto = clave.text.toString()
            val claveConfirmacionTexto = clave2.text.toString()

            val regex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,}$")

            if (!regex.matches(claveTexto)) {
                aviso.text = "La contraseña debe tener al menos 8 caracteres, con al menos una mayúscula, una minúscula y un número"
            } else if (claveTexto != claveConfirmacionTexto) {
                aviso.text = "Las contraseñas no coinciden"
            } else {

                val fila = bd.rawQuery(
                    "SELECT nombre FROM usuarios WHERE nombre = ?", arrayOf(nombreUsuarioTexto)
                )

                if (fila.moveToFirst()) {
                    aviso.text = "Este usuario ya existe"
                } else {

                    val registroUsuario = ContentValues().apply {
                        put("nombre", nombreUsuarioTexto)
                        put("clave", claveTexto)
                    }
                    val idUsuario = bd.insert("usuarios", null, registroUsuario)

                    if (idUsuario.toInt() != -1) {

                        val registroEstadisticas = ContentValues().apply {
                            put("id_usuario", idUsuario.toInt())
                            put("dadosLanzados", 0)
                            put("monedasLanzadas", 0)
                        }
                        bd.insert("estadisticas", null, registroEstadisticas)

                        Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("idUsuario", idUsuario.toInt())
                        startActivity(intent)
                    } else {
                        aviso.text = "Error al registrar el usuario"
                    }
                }

                fila.close()

            }
        }

        // Navegación
        botonLogIn.setOnClickListener {
            val intento = Intent(this, Login::class.java)
            startActivity(intento)
        }
    }
}