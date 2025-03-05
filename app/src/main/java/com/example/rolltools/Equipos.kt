package com.example.rolltools

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Equipos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_equipos)

        val bundle = intent.extras
        val idUsuario = bundle?.getInt("idUsuario")

        var tvEquipo1 = findViewById<TextView>(R.id.team1)
        var tvEquipo2 = findViewById<TextView>(R.id.team2)
        var equipo1 = intent.getStringArrayListExtra("equipo1")
        var equipo2 = intent.getStringArrayListExtra("equipo2")


        tvEquipo1.text = equipo1?.joinToString(separator = "\n") ?: "Equipo 1 vacío"
        tvEquipo2.text = equipo2?.joinToString(separator = "\n") ?: "Equipo 2 vacío"

        //Navegacion

        var irPartidas = findViewById<Button>(R.id.btnVolver)
        irPartidas.setOnClickListener {
            var intento = Intent(this, Participantes::class.java)
            intento.putExtra("idUsuario", idUsuario)
            startActivity(intento)
        }

        val irHome = findViewById<ImageButton>(R.id.btnHome)
        irHome.setOnClickListener {
            val intento = Intent(this, MainActivity::class.java)
            intento.putExtra("idUsuario", idUsuario)
            startActivity(intento)
        }

        val verPopupMenu = findViewById<ImageButton>(R.id.btnMenuPop)
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
        }
    }
}