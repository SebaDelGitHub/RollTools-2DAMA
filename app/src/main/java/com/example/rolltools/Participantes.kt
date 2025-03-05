package com.example.rolltools

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Participantes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_participantes)

        val etParticipantes = findViewById<EditText>(R.id.etParticipantes)
        val generar = findViewById<Button>(R.id.btnGenerar)

        val bundle = intent.extras
        val idUsuario = bundle?.getInt("idUsuario")


        generar.setOnClickListener {

            var participantes = etParticipantes.text.toString().split("\n").filter { it.isNotBlank() } //Evitar las lineas vacias
            var nombresMezclados = participantes.shuffled()


            var equipo1 = ArrayList<String>()
            var equipo2 = ArrayList<String>()
            nombresMezclados.forEachIndexed { index, nombre ->
                if (index % 2 == 0) {
                    equipo1.add(nombre)
                } else {
                    equipo2.add(nombre)
                }
            }


            val intent = Intent(this, Equipos::class.java)
            intent.putExtra("idUsuario", idUsuario)
            intent.putStringArrayListExtra("equipo1", equipo1)
            intent.putStringArrayListExtra("equipo2", equipo2)
            startActivity(intent)
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