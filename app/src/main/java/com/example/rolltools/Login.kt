package com.example.rolltools

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var idUsuario: Int = -1
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val nombreUsuario = findViewById<EditText>(R.id.et1)
        val clave = findViewById<EditText>(R.id.password)

        val botonLogIn = findViewById<Button>(R.id.buttonLog)
        val botonSingIn = findViewById<Button>(R.id.buttonSing)

        var aviso = findViewById<TextView>(R.id.tv1)

        Glide.with(this)
            .asGif()
            .load(R.drawable.orange)
            .into(findViewById(R.id.backgroundGif))
        Glide.with(this)
            .asGif()
            .load(R.drawable.orange)
            .into(findViewById(R.id.backgroundGif2))
        Glide.with(this)
            .asGif()
            .load(R.drawable.orange)
            .into(findViewById(R.id.backgroundGif3))



        botonLogIn.setOnClickListener {
            val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
            val bd = admin.writableDatabase

            val nombreIntroducido = nombreUsuario.text.toString()
            val claveIntroducida = clave.text.toString()


            val fila = bd.rawQuery(
                "SELECT id, clave FROM usuarios WHERE nombre = ?", arrayOf(nombreIntroducido)
            )

            if (fila.moveToFirst()) {
                idUsuario = fila.getInt(0)
                val claveCorrecta = fila.getString(1)

                if (claveCorrecta == claveIntroducida) {

                    val intento = Intent(this, MainActivity::class.java)
                    intento.putExtra("idUsuario", idUsuario)
                    startActivity(intento)

                    Toast.makeText(
                        this,
                        "Ha realizado el inicio de sesión exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    aviso.setText("Contraseña incorrecta")
                }
            } else {
                aviso.setText("No existe este usuario")
            }

            fila.close()

        }

        //Navegacion
        botonSingIn.setOnClickListener{
            val intento = Intent(this, Registro::class.java)
            startActivity(intento)
        }

    }

    }