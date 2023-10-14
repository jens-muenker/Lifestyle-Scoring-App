package com.frosch2010.lifestyle_scoring_app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.PlayerDTO

class PlayerAdapter(private var players: List<PlayerDTO>, private val callback: OnPlayerClickedListener) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnPlayerClickedListener {
        fun onPlayerClicked(index: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lst_item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = players[position]

        holder.itemView.findViewById<TextView>(R.id.card_name).text = item.name
        holder.itemView.setOnClickListener {
            callback.onPlayerClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return players.size
    }

    fun updateData(newPlayers: List<PlayerDTO>) {
        players = newPlayers
        notifyDataSetChanged()
    }
}