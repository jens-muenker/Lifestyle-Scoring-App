package com.frosch2010.lifestyle_scoring_app.ui.adapters

import android.content.Context
import android.graphics.PorterDuff
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.ui.dialogs.DeletePlayerCardDialog
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.PlayerCardsViewModel
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.CardDTO

/**
 * This adapter is used to display the cards of a player in a [RecyclerView].
 * @author Jens Münker
 */
class PlayerCardsAdapter(private var cards: List<CardDTO>, private val context: Context, private val viewModel: PlayerCardsViewModel) : RecyclerView.Adapter<PlayerCardsAdapter.ViewHolder>(), DeletePlayerCardDialog.IDeletePlayerCardDialogCallback {

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

        val color = when(item.cardType){
            CardTypeEnum.CAR -> ContextCompat.getColor(context, R.color.card_car)
            CardTypeEnum.HOUSE -> ContextCompat.getColor(context, R.color.card_house)
            CardTypeEnum.JOB -> ContextCompat.getColor(context, R.color.card_job)
            CardTypeEnum.ANIMAL -> ContextCompat.getColor(context, R.color.card_animal)
            CardTypeEnum.LOVE -> ContextCompat.getColor(context, R.color.card_love)
            CardTypeEnum.SPORT -> ContextCompat.getColor(context, R.color.card_sport)
        }

        holder.itemView.findViewById<LinearLayout>(R.id.card_linear).background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)

        holder.itemView.setOnLongClickListener {
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.menuInflater.inflate(R.menu.reuse_menu_delete, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                if (menuItem.itemId == R.id.reuse_menu_delete) {
                    val deletePlayerCardDialog = DeletePlayerCardDialog(context, this)
                    deletePlayerCardDialog.show(context.getString(R.string.delete_card), context.getString(R.string.message_delete_card), position)
                }
                true
            }
            popupMenu.gravity = Gravity.END
            popupMenu.show()
            true
        }

        if(item.icon != null) {
            val image = holder.itemView.findViewById<ImageView>(R.id.card_icon)
            image.setImageDrawable(AppCompatResources.getDrawable(context, item.icon))
            image.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    fun updateData(newCards: List<CardDTO>) {
        cards = newCards
        notifyDataSetChanged()
    }

    override fun onDeleteConfirmed(cardIndex: Int) {
        viewModel.deleteCard(cardIndex)
    }
}