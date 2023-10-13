package com.frosch2010.lifestyle_scoring_app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.CardDTO

class PlayerCardsAdapter(private var cards: List<CardDTO>) : RecyclerView.Adapter<PlayerCardsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lst_item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cards[position]
        holder.itemView.findViewById<TextView>(R.id.card_name).text = item.name
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    fun updateData(newCards: List<CardDTO>) {
        cards = newCards
        notifyDataSetChanged()
    }
}