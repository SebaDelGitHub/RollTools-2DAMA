package com.example.rolltools

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        var irDado = findViewById<ImageButton>(R.id.btnDado)
        var irMarcador = findViewById<ImageButton>(R.id.btnMarcador)
        var irEquipos = findViewById<ImageButton>(R.id.btnEquipos)
        var irMoneda = findViewById<ImageButton>(R.id.btnMoneda)
        var irPartidas = findViewById<ImageButton>(R.id.btnPartida)
        var verPopupMenu = findViewById<ImageButton>(R.id.btnMenuPop)


        val bundle = intent.extras
        val idUsuario = bundle?.getInt("idUsuario")


        //Navegacion
        irDado.setOnClickListener {
            var intento = Intent(this, Dado::class.java)
            intento.putExtra("idUsuario", idUsuario)
            startActivity(intento)
        }



        irMarcador.setOnClickListener {
            var intento = Intent(this, Marcador::class.java)
            intento.putExtra("idUsuario", idUsuario)
            startActivity(intento)
        }

        irEquipos.setOnClickListener {
            var intento = Intent(this, Participantes::class.java)
            intento.putExtra("idUsuario", idUsuario)
            startActivity(intento)
        }

        irMoneda.setOnClickListener {
            var intento = Intent(this, Moneda::class.java)
            intento.putExtra("idUsuario", idUsuario)
            startActivity(intento)
        }

        irPartidas.setOnClickListener {
            var intento = Intent(this, Partidas::class.java)
            intento.putExtra("idUsuario", idUsuario)
            startActivity(intento)
        }


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