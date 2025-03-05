package com.example.rolltools

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.util.Locale
import kotlin.math.sqrt

class Dado : AppCompatActivity() {

    // Variables para el sensor
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var sensorListener: SensorEventListener
    private var shakeThreshold = 15.0f //resistencia de agitacion
    private var lastShakeTime: Long = 0

    //-------------------------------------------
    //Para la funcion de voz
    private val SPEECH_REQUEST_CODE = 1
    private lateinit var tvNum: TextView

    //Usuario
    private lateinit var bundle: Bundle
    private  var idUsuario: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dado)

        var botonLanzar = findViewById<Button>(R.id.button)
        tvNum = findViewById<TextView>(R.id.tvNum)

         bundle = intent.extras!!
         idUsuario = bundle?.getInt("idUsuario")!!

        val botonMicrofono = findViewById<ImageButton>(R.id.btnMicro)


        //Lanzar el dado usando el boton
        botonLanzar.setOnClickListener {
            if (idUsuario != null) {
                lanzarDado(tvNum, idUsuario)
            }
        }

        //Pulsar boton de voz
        botonMicrofono.setOnClickListener {
            startSpeechToText()
        }


        //Sensor y acelerometer
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null) {
                    var x = event.values[0]
                    var y = event.values[1]
                    var z = event.values[2]

                    var magnitud =
                        sqrt((x * x + y * y + z * z).toDouble()).toFloat() //combina las lecturas de los tres ejes en un único valor

                    if (magnitud >= shakeThreshold) {
                        val timepoTransucrrido = System.currentTimeMillis()

                        if (timepoTransucrrido - lastShakeTime > 3000) { //evitar multiples detecciones rápidas
                            lastShakeTime = timepoTransucrrido
                            if (idUsuario != null) {
                                lanzarDado(tvNum, idUsuario)
                            }
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                //???
            }


        }

        sensorManager.registerListener(
            sensorListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )

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

    private fun lanzarDado(tvNum: TextView, idUsuario: Int) {
        val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
        val bd = admin.writableDatabase

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_dado)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val gifImageView = dialog.findViewById<ImageView>(R.id.gif_dado)
        Glide.with(this)
            .asGif()
            .load(R.drawable.dadogif)
            .into(gifImageView)

        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()


            val numeroAleatorio = (1..6).random()


            Handler(Looper.getMainLooper()).postDelayed({
                tvNum.text = numeroAleatorio.toString()
            }, 200)

            val fila = bd.rawQuery(
                "SELECT dadosLanzados FROM estadisticas WHERE id_usuario = ?",
                arrayOf(idUsuario.toString())
            )

            if (fila.moveToFirst()) {
                val dadosLanzados = fila.getInt(0) + 1


                val valores = ContentValues()
                valores.put("dadosLanzados", dadosLanzados)

                bd.update("estadisticas", valores, "id_usuario = ?", arrayOf(idUsuario.toString()))
            }

            fila.close()

        }, 3000)
    }

    //Reconocimiento de voz
    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

            putExtra(RecognizerIntent.EXTRA_PROMPT, "Di, lanzar dado")
        }
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(
                this, "El reconocimiento de voz no está disponible",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {

            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            result?.let {


                if (it[0].toLowerCase() == "lanzar dado") {

                    if (idUsuario != null) {
                        lanzarDado(tvNum, idUsuario)
                    }
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(sensorListener)
    }
}