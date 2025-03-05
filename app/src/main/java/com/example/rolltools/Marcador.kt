package com.example.rolltools

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class Marcador : AppCompatActivity() {
    private val SPEECH_REQUEST_CODE = 1
    private var puntosLocal = 0
    private var puntosVisitante = 0

    //Equipo seleccionado por voz
    private lateinit var equipoVoz: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_marcador)


        var sumarLocal = findViewById<Button>(R.id.btnSumarLocal)
        var restarLocal = findViewById<Button>(R.id.btnRestarLocal)
        var reiniciarLocal = findViewById<Button>(R.id.btnReiniciarLocal)
        var sumarVisitante = findViewById<Button>(R.id.btnSumarVisitante)
        var restarVisitante = findViewById<Button>(R.id.btnRestarVisitante)
        var reiniciarVisitante = findViewById<Button>(R.id.btnReiniciarVisitante)
        var vozLocal = findViewById<ImageButton>(R.id.btnVozLocal)
        var vozVisitante = findViewById<ImageButton>(R.id.btnVozVisitante)

        val tvLocal = findViewById<TextView>(R.id.tvLocal)
        val tvVisitante = findViewById<TextView>(R.id.tvVisitante)

        val bundle = intent.extras
        val idUsuario = bundle?.getInt("idUsuario")


        sumarLocal.setOnClickListener {
            puntosLocal += 1
            tvLocal.setText(puntosLocal.toString())
        }

        restarLocal.setOnClickListener {
            if (puntosLocal > 0) {
                puntosLocal -= 1
            }
            tvLocal.setText(puntosLocal.toString())
        }

        reiniciarLocal.setOnClickListener {
            puntosLocal = 0
            tvLocal.setText(puntosLocal.toString())
        }

        sumarVisitante.setOnClickListener {
            puntosVisitante += 1
            tvVisitante.setText(puntosVisitante.toString())
        }

        restarVisitante.setOnClickListener {
            if (puntosVisitante > 0) {
                puntosVisitante -= 1
            }
            tvVisitante.setText(puntosVisitante.toString())
        }

        reiniciarVisitante.setOnClickListener {
            puntosVisitante = 0
            tvVisitante.setText(puntosVisitante.toString())
        }


        vozLocal.setOnClickListener {
            equipoVoz = "local"
            startSpeechToText(equipoVoz)
        }

        vozVisitante.setOnClickListener {
            equipoVoz = "visitante"
            startSpeechToText(equipoVoz)
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


    private fun startSpeechToText(equipo: String) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Di un número entre 1 y 10 para sumar puntos al equipo $equipo"
            )
        }
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, "El reconocimiento de voz no está disponible", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            result?.let {
                val palabraPuntos = it[0].toLowerCase()


                val puntos = when (palabraPuntos) {
                    "uno" -> 1
                    "dos" -> 2
                    "tres" -> 3
                    "cuatro" -> 4
                    "cinco" -> 5
                    "seis" -> 6
                    "siete" -> 7
                    "ocho" -> 8
                    "nueve" -> 9
                    "diez" -> 10
                    else -> 0
                }

                if (puntos > 0) {
                    if (equipoVoz == "local") {
                        puntosLocal += puntos
                        val tvLocal = findViewById<TextView>(R.id.tvLocal)
                        tvLocal.text = puntosLocal.toString()

                    } else if (equipoVoz == "visitante") {
                        puntosVisitante += puntos
                        val tvVisitante = findViewById<TextView>(R.id.tvVisitante)
                        tvVisitante.text = puntosVisitante.toString()
                    }
                }
            }
        }
    }
}
