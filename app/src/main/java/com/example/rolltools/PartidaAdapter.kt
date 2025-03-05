package com.example.rolltools

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Partida(
    val id: Int,
    val titulo: String,
    val fecha: String,
    val juego: String,
    val imagen: ByteArray? = null
)

class PartidaAdapter(
    private val partidas: List<Partida>,
    private val onClick: (Partida) -> Unit
) : RecyclerView.Adapter<PartidaAdapter.PartidaViewHolder>() {

    class PartidaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.tvTituloPartida)
        val fecha: TextView = view.findViewById(R.id.tvFechaPartida)
        val juego: TextView =
            view.findViewById(R.id.tvJuegoPartida)
        val imagen: ImageView = view.findViewById(R.id.iv1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_partida, parent, false)
        return PartidaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PartidaViewHolder, position: Int) {
        val partida = partidas[position]
        holder.titulo.text = partida.titulo
        holder.fecha.text = partida.fecha
        holder.juego.text = partida.juego


        if (partida.imagen != null) {
            val bitmap = BitmapFactory.decodeByteArray(partida.imagen, 0, partida.imagen.size)
            holder.imagen.setImageBitmap(bitmap)
        } else {
            holder.imagen.setImageResource(R.drawable.placeimg)
        }

        holder.itemView.setOnClickListener { onClick(partida) }
    }

    override fun getItemCount(): Int = partidas.size
}
