package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.frosch2010.lifestyle_scoring_app.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * This dialog is used to select an item from a list of items with icons.
 * @author Jens Münker
 */
class ListDialogWithIcons(private val context: Context, private val title: String, private val items: List<Pair<String, Int>>, private val listener: OnItemSelectedListener) :
    BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = convertView ?: LayoutInflater.from(context).inflate(R.layout.lst_item_dialog_with_icon, parent, false)

        val iconImageView = rowView.findViewById<ImageView>(R.id.iconImageView)
        val textTextView = rowView.findViewById<TextView>(R.id.textTextView)

        val item = getItem(position) as Pair<Int, String>
        iconImageView.setImageResource(item.first)
        textTextView.text = item.second

        return rowView
    }

    fun show() {
        val builder = MaterialAlertDialogBuilder(context)

        builder.setTitle(title)


        builder.setAdapter(this) { _, which ->
            val selected = items[which]
            listener.onItemSelected(selected.first)
        }

        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }

    interface OnItemSelectedListener {
        fun onItemSelected(item: String)
    }
}