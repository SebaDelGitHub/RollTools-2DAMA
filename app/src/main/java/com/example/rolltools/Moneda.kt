package com.example.rolltools

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class Moneda : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_moneda)

        val botonLanzar = findViewById<Button>(R.id.button)
        var tvCaraCruz = findViewById<TextView>(R.id.tvCaraCruz)
        var tvResultado = findViewById<TextView>(R.id.tvResultado)
        val bundle = intent.extras
        val idUsuario = bundle?.getInt("idUsuario")

        val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
        val bd = admin.writableDatabase


        botonLanzar.setOnClickListener {

            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_dado)
            dialog.setCancelable(false)

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val gifImageView = dialog.findViewById<ImageView>(R.id.gif_dado)
            Glide.with(this)
                .asGif()
                .load(R.drawable.monedagif)
                .into(gifImageView)

            dialog.show()

            Handler(Looper.getMainLooper()).postDelayed({
                dialog.dismiss()

                val esCara = (0..1).random() == 0

                var resultadoTexto = if (esCara) "CARA" else "CRUZ"
                var resultadoSimbolo = if (esCara) "㋛" else "+"

                Handler(Looper.getMainLooper()).postDelayed({
                    tvCaraCruz.text = resultadoTexto
                    tvResultado.text = resultadoSimbolo
                }, 200)

                //Actualizar la estadística
                val fila = bd.rawQuery(
                    "SELECT monedasLanzadas FROM estadisticas WHERE id_usuario = ?",
                    arrayOf(idUsuario.toString())
                )

                if (fila.moveToFirst()) {
                    val monedasLanzadas = fila.getInt(0) + 1

                    // Actualizar el valor en la tabla
                    val valores = ContentValues()
                    valores.put("monedaslanzadas", monedasLanzadas)

                    bd.update(
                        "estadisticas",
                        valores,
                        "id_usuario = ?",
                        arrayOf(idUsuario.toString())
                    )
                }

                fila.close()
            }, 3000)
        }

// Navegación
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