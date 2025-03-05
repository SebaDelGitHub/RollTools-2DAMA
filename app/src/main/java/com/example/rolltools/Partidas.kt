package com.example.rolltools

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton



class Partidas : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var irAgregar: FloatingActionButton
    private val partidas = mutableListOf<Partida>()
    private lateinit var adapter: PartidaAdapter

    private var idUsuario: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partidas)

        val bundle = intent.extras
        idUsuario = bundle?.getInt("idUsuario")!!

        recyclerView = findViewById(R.id.recyclerViewPartidas)
        irAgregar = findViewById(R.id.btnAgregar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PartidaAdapter(partidas) { partida -> irADetalles(partida) }
        recyclerView.adapter = adapter

        cargarPartidas()

        irAgregar.setOnClickListener {
            val intent = Intent(this, AgregarPartida::class.java)
            intent.putExtra("idUsuario",idUsuario)
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

    private fun cargarPartidas() {
        val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
        val db = admin.readableDatabase
        val cursor = db.rawQuery("SELECT id_partida, titulo, fecha, juego, imagen FROM partidas WHERE id_usuario = ?",
            arrayOf(idUsuario.toString())
        )

        partidas.clear()
        if (cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndex("id_partida")
                val tituloIndex = cursor.getColumnIndex("titulo")
                val fechaIndex = cursor.getColumnIndex("fecha")
                val juegoIndex = cursor.getColumnIndex("juego")
                val imagenIndex = cursor.getColumnIndex("imagen")

                if (idIndex != -1 && tituloIndex != -1 && fechaIndex != -1 && juegoIndex != -1 && imagenIndex != -1) {
                    val id = cursor.getInt(idIndex)
                    val titulo = cursor.getString(tituloIndex)
                    val fecha = cursor.getString(fechaIndex)
                    val juego = cursor.getString(juegoIndex)
                    val imagen = cursor.getBlob(imagenIndex)

                    partidas.add(Partida(id, titulo, fecha, juego, imagen))
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        adapter.notifyDataSetChanged()
    }

    private fun irADetalles(partida: Partida) {
        val intent = Intent(this, DetallesPartida::class.java)
        intent.putExtra("idPartida", partida.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        cargarPartidas()
    }
}