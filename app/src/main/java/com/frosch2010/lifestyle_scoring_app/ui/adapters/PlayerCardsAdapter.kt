package com.frosch2010.lifestyle_scoring_app.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.CardDTO

class PlayerCardsAdapter(private var cards: List<CardDTO>, private val context: Context) : RecyclerView.Adapter<PlayerCardsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lst_item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cards[position]

        holder.itemView.findViewById<TextView>(R.id.card_name).text = item.name

        if(item.cardType == CardTypeEnum.CAR) {
            holder.itemView.findViewById<TextView>(R.id.card_points).text = context.getString(R.string.points, item.points.toString())
        } else {
            holder.itemView.findViewById<TextView>(R.id.card_points).visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    fun updateData(newCards: List<CardDTO>) {
        cards = newCards
        notifyDataSetChanged()
    }
}