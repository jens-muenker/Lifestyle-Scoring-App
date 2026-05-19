package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.DialogManualAddCardBinding
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.utils.CardNameMatcher
import com.frosch2010.lifestyle_scoring_app.utils.InputValidator
import com.frosch2010.lifestyle_scoring_app.utils.ManualCardNameQueryResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Legacy searchable dialog listing every manual card option before the dedicated manual-add activity existed.
 *
 * @param context Theme context for inflation and localized composite strings.
 * @param cardMap Localized label to [ICard] map used to build selectable rows.
 * @param onCardResolved Invoked with the concrete card when the user taps a filtered row.
 */
class ManualAddCardDialog(
    private val context: Context,
    private val cardMap: Map<String, ICard>,
    private val onCardResolved: (ICard) -> Unit
) {

    /**
     * Internal row model combining display text, normalized search text, and the backing card instance.
     *
     * @property displayText Two-line list label combining card name and category label.
     * @property searchableText Normalized token material for substring filtering.
     * @property card Domain payload returned on item click.
     */
    private data class ManualCardOption(
        val displayText: String,
        val searchableText: String,
        val card: ICard
    )

    /**
     * Inflates the custom layout, wires live filtering, and forwards selection to [onCardResolved].
     */
    fun show() {
        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle(context.getString(R.string.manual_add_card_title))

        val binding = DialogManualAddCardBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)

        builder.setNegativeButton(context.getString(R.string.abort)) { dialog, _ ->
            dialog.dismiss()
        }

        val allOptions = cardMap.entries
            .map { entry ->
                val cardName = entry.key
                val typeName = getCardTypeLabel(entry.value)
                val displayText = context.getString(R.string.manual_card_list_item, cardName, typeName)
                ManualCardOption(
                    displayText = displayText,
                    searchableText = CardNameMatcher.cleanText("$cardName $typeName"),
                    card = entry.value
                )
            }
            .sortedBy { it.displayText }
        val filteredOptions = allOptions.toMutableList()

        binding.listManualCards.emptyView = binding.textManualNoResults

        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_list_item_1,
            filteredOptions.map { it.displayText }.toMutableList()
        )
        binding.listManualCards.adapter = adapter

        val dialog = builder.create()
        dialog.show()

        binding.editManualCardSearch.doOnTextChanged { text, _, _, _ ->
            filteredOptions.clear()
            when (val queryResult = InputValidator.validateManualCardNameQuery(text?.toString().orEmpty())) {
                ManualCardNameQueryResult.Empty,
                ManualCardNameQueryResult.TooShort -> filteredOptions.addAll(allOptions)
                is ManualCardNameQueryResult.Valid -> {
                    val query = CardNameMatcher.cleanText(queryResult.trimmedQuery)
                    filteredOptions.addAll(
                        allOptions.filter { option -> option.searchableText.contains(query) }
                    )
                }
            }
            adapter.clear()
            adapter.addAll(filteredOptions.map { it.displayText })
        }

        binding.listManualCards.setOnItemClickListener { _, _, position, _ ->
            onCardResolved(filteredOptions[position].card)
            dialog.dismiss()
        }
    }

    /**
     * @param card Card whose [CardTypeEnum] selects the short category caption appended in list rows.
     * @return Localized category label for the second line of the composite display string.
     */
    private fun getCardTypeLabel(card: ICard): String =
        when (card.cardType) {
            CardTypeEnum.ANIMAL -> context.getString(R.string.points_category_animal)
            CardTypeEnum.CAR -> context.getString(R.string.points_category_car)
            CardTypeEnum.HOUSE -> context.getString(R.string.points_category_house)
            CardTypeEnum.JOB -> context.getString(R.string.points_category_job)
            CardTypeEnum.SPORT -> context.getString(R.string.points_category_sport)
            CardTypeEnum.LOVE -> context.getString(R.string.points_category_love)
        }
}
