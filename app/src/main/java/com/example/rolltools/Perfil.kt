package com.example.rolltools

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class Perfil : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)

        var tvNombre = findViewById<TextView>(R.id.tvNombre)
        var tvClave = findViewById<TextView>(R.id.tvClave)
        var tvDadosLanzados = findViewById<TextView>(R.id.tvDadosLanzados)
        var tvMonedasLanzadas = findViewById<TextView>(R.id.tvMonedasLanzadas)

        var borrarCuenta = findViewById<Button>(R.id.btnBorrarCuenta)
        var btnCambiarClave = findViewById<Button>(R.id.btnCambiarClave)
        var botonReiniciar = findViewById<Button>(R.id.btnReiniciar)
        var botonLogOut = findViewById<Button>(R.id.btnLogout)


        botonLogOut.setOnClickListener{
            val intento = Intent(this, Login::class.java)
            startActivity(intento)
        }


        botonReiniciar.setOnClickListener {
            val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
            val bd = admin.writableDatabase

            val bundle = intent.extras
            val idUsuario = bundle?.getInt("idUsuario")

            if (idUsuario != null) {

                val valoresReiniciados = ContentValues().apply {
                    put("dadosLanzados", 0)
                    put("monedasLanzadas", 0)
                }

                val filasActualizadas = bd.update(
                    "estadisticas",
                    valoresReiniciados,
                    "id_usuario = ?",
                    arrayOf(idUsuario.toString())
                )

                if (filasActualizadas > 0) {
                    Toast.makeText(this, "Estadísticas reiniciadas", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Error: ID de usuario no encontrado reinicia la app", Toast.LENGTH_SHORT).show() //no deberia salir
            }


        }


        btnCambiarClave.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Cambiar Contraseña")

            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(50, 40, 50, 10)


            val claveActualInput = EditText(this)
            claveActualInput.hint = "Contraseña Actual"
            claveActualInput.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            layout.addView(claveActualInput)


            val newPasswordInput = EditText(this)
            newPasswordInput.hint = "Nueva Contraseña"
            newPasswordInput.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            layout.addView(newPasswordInput)

            builder.setView(layout)

            builder.setPositiveButton("Cambiar") { dialog, _ ->
                val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
                val bd = admin.writableDatabase

                val bundle = intent.extras
                val idUsuario = bundle?.getInt("idUsuario")

                val claveActual = claveActualInput.text.toString()
                val nuevaClave = newPasswordInput.text.toString()

                val fila = bd.rawQuery(
                    "SELECT clave FROM usuarios WHERE id = ?",
                    arrayOf(idUsuario.toString())
                )

                if (fila.moveToFirst()) {
                    val claveCorrecta = fila.getString(0)

                    if (claveCorrecta == claveActual) {

                        val regex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,}$")
                        if (regex.matches(nuevaClave)) {

                            val values = ContentValues()
                            values.put("clave", nuevaClave)
                            bd.update("usuarios", values, "id = ?", arrayOf(idUsuario.toString()))

                            Toast.makeText(
                                this,
                                "Contraseña actualizada correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "La nueva contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas y números.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "La contraseña actual es incorrecta",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "No se encontró el usuario", Toast.LENGTH_SHORT).show()
                }

                fila.close()

                dialog.dismiss()
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }


            builder.create().show()
        }

//----------------------------------------------------------------------------------------------------------------
        borrarCuenta.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("CUIDADO!! Borrado")
            builder.setMessage("CUIDADO: ¿Estás seguro de que quieres borrar tu cuenta? Esta acción es irreversible (Mucho tiempo).")

            //EditText del dialog
            val input = EditText(this)
            input.hint = "Introduce tu contraseña"
            input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            builder.setView(input)

            builder.setPositiveButton("Borrar") { dialog, _ ->
                val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
                val bd = admin.writableDatabase

                val bundle = intent.extras
                val idUsuario = bundle?.getInt("idUsuario")
                val passwordIntroducida = input.text.toString()


                val fila = bd.rawQuery(
                    "SELECT clave FROM usuarios WHERE id = ?",
                    arrayOf(idUsuario.toString())
                )

                if (fila.moveToFirst()) {
                    val claveCorrecta = fila.getString(0)

                    if (claveCorrecta == passwordIntroducida) {

                        bd.delete("usuarios", "id = ?", arrayOf(idUsuario.toString()))
                        bd.delete("estadisticas", "id_usuario = ?", arrayOf(idUsuario.toString()))
                        Toast.makeText(this, "Cuenta eliminada correctamente", Toast.LENGTH_SHORT)
                            .show()

                        val intento = Intent(this, Login::class.java)
                        startActivity(intento)
                        finish()
                    } else {
                        Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Error: ID de usuario no encontrado reinicia la app", Toast.LENGTH_SHORT).show()
                }

                fila.close()


                dialog.dismiss()
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }


            builder.create().show()
        }


        //Cargar datos
        val bundle = intent.extras
        val idUsuario = bundle?.getInt("idUsuario")

        if (idUsuario != null) {
            val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
            val bd = admin.writableDatabase


            val fila = bd.rawQuery(
                """
                SELECT u.nombre, u.clave, e.dadosLanzados, e.monedasLanzadas 
                FROM usuarios u 
                INNER JOIN estadisticas e 
                ON u.id = e.id_usuario
                WHERE u.id = ?
                """,
                arrayOf(idUsuario.toString())
            )


            if (fila.moveToFirst()) {
                var nombre = fila.getString(0)
                var clave = fila.getString(1)
                var dadosLanzados = fila.getInt(2)
                var monedasLanzadas = fila.getInt(3)

                tvNombre.text = nombre
                tvClave.text = clave
                tvDadosLanzados.text = dadosLanzados.toString()
                tvMonedasLanzadas.text = monedasLanzadas.toString()
            }

            fila.close()

        } else {

            Toast.makeText(this, "Error: ID de usuario no encontrado reinicia la app", Toast.LENGTH_SHORT).show()
        }

        //Navegacion
        val irHome = findViewById<ImageButton>(R.id.btnHome)
        irHome.setOnClickListener {
            val intento = Intent(this, MainActivity::class.java)
            intento.putExtra("idUsuario", idUsuario)
            startActivity(intento)
        }

        var verPopupMenu = findViewById<ImageButton>(R.id.btnMenuPop)
        verPopupMenu.setOnClickListener {
            val popupMenu = PopupMenu(this, verPopupMenu)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)


            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.option1 -> {

                        val intent = Intent(this, Perfil::class.java)
                        intent.putExtra("idUsuario", idUsuario)
                        startActivity(intent)
                        true
                    }
                    R.id.option2 -> {

                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }


    }
}