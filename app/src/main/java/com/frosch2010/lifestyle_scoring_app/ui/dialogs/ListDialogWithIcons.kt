package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.frosch2010.lifestyle_scoring_app.databinding.LstItemDialogWithIconBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Picker dialog whose rows pair a leading drawable with a label, implemented as both [BaseAdapter] and dialog host.
 *
 * @param context Theme context for inflation and Material theming.
 * @param title Dialog title displayed above the list.
 * @param items `(label, drawableResId)` tuples rendered per row.
 * @param listener Receiver invoked with the chosen label (icon is implied by lookup upstream).
 */
class ListDialogWithIcons(
    private val context: Context,
    private val title: String,
    private val items: List<Pair<String, Int>>,
    private val listener: OnItemSelectedListener
) : BaseAdapter() {

    /**
     * @return Number of selectable rows.
     */
    override fun getCount(): Int {
        return items.size
    }

    /**
     * @param position Row index.
     * @return The backing pair instance at [position].
     */
    override fun getItem(position: Int): Any {
        return items[position]
    }

    /**
     * @param position Row index.
     * @return Stable row id (`position` cast to long).
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /**
     * Inflates or binds `LstItemDialogWithIcon` rows for the alert dialog list.
     *
     * @param position Row to render.
     * @param convertView Recycled row root, if any.
     * @param parent List parent supplied by the dialog list container.
     * @return Row root view with icon and text populated.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = if (convertView == null) {
            LstItemDialogWithIconBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            LstItemDialogWithIconBinding.bind(convertView)
        }

        val item = items[position]
        binding.iconImageView.setImageResource(item.second)
        binding.textTextView.text = item.first
        binding.root.contentDescription = item.first

        return binding.root
    }

    /**
     * Shows a non-cancelable dialog using this object as the list adapter.
     */
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

    /**
     * Callback receiving the human-readable label for the tapped row.
     */
    interface OnItemSelectedListener {
        /**
         * @param item Label portion of the selected pair.
         */
        fun onItemSelected(item: String)
    }
}
