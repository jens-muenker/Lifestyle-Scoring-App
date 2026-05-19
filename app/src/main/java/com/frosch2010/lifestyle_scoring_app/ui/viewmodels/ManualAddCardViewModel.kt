package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetCardNameUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetCardsMapUseCase
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.ManualCardOptionDTO
import com.frosch2010.lifestyle_scoring_app.utils.CardNameMatcher
import com.frosch2010.lifestyle_scoring_app.utils.InputValidator
import com.frosch2010.lifestyle_scoring_app.utils.ManualCardNameQueryResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
/**
 * Builds and filters the manual card catalog: excludes already-owned cards and applies substring search rules.
 *
 * @param getCardsMapUseCase Supplies the authoritative localized label-to-card map.
 * @param getCardNameUseCase Resolves derived titles (notably for love cards) independent of map keys.
 */
@HiltViewModel
class ManualAddCardViewModel @Inject constructor(
    private val getCardsMapUseCase: GetCardsMapUseCase,
    private val getCardNameUseCase: GetCardNameUseCase
) : ViewModel() {

    /**
     * RecyclerView-ready slice of the catalog after exclusions and optional query filtering.
     *
     * @property visibleCards Rows currently shown in the manual picker list.
     * @property isEmptyStateVisible `true` when [visibleCards] is empty and the placeholder should show.
     */
    data class UiState(
        val visibleCards: List<ManualCardOptionDTO> = emptyList(),
        val isEmptyStateVisible: Boolean = true
    )

    /**
     * Cached full option list after ownership exclusion; filtering mutates only the published subset.
     */
    private var allCards: List<ManualCardOptionDTO> = emptyList()

    private val _uiState = MutableLiveData(UiState())
    val uiState: LiveData<UiState> = _uiState

    /**
     * Hydrates [allCards] minus names already held by the player, then resets the visible filter.
     *
     * @param excludedCardNames Normalized searchable tokens (typically already-owned card fingerprints).
     */
    fun load(excludedCardNames: Set<String>) {
        allCards = getCardOptions()
            .filterNot { option -> excludedCardNames.contains(option.searchableText) }
        applyFilter("")
    }

    /**
     * Recomputes the visible subset whenever the search field changes.
     *
     * @param queryRaw Raw text from the search `TextView` or `EditText`.
     */
    fun onSearchQueryChanged(queryRaw: String) {
        applyFilter(queryRaw)
    }

    /**
     * Applies validation rules: blank or too-short queries show the full remaining catalog; valid queries substring-match.
     *
     * @param queryRaw Unmodified user input forwarded to [InputValidator.validateManualCardNameQuery].
     */
    private fun applyFilter(queryRaw: String) {
        val filtered = when (val queryResult = InputValidator.validateManualCardNameQuery(queryRaw)) {
            ManualCardNameQueryResult.Empty,
            ManualCardNameQueryResult.TooShort -> allCards
            is ManualCardNameQueryResult.Valid -> {
                val query = CardNameMatcher.cleanText(queryResult.trimmedQuery)
                allCards.filter { option -> option.searchableText.contains(query) }
            }
        }
        _uiState.value = UiState(
            visibleCards = filtered,
            isEmptyStateVisible = filtered.isEmpty()
        )
    }

    /**
     * @return Sorted [ManualCardOptionDTO] rows including icon metadata and normalized search tokens.
     */
    private fun getCardOptions(): List<ManualCardOptionDTO> {
        val cardsMap = getCardsMapUseCase()
        return cardsMap.entries
            .map { entry ->
                val iconRes = getTypeIcon(entry.value.cardType)
                val displayName = if (entry.value is LoveCard) {
                    getCardNameUseCase(entry.value)
                } else {
                    entry.key
                }
                ManualCardOptionDTO(
                    name = displayName,
                    cardType = entry.value.cardType,
                    iconRes = iconRes,
                    searchableText = CardNameMatcher.cleanText(displayName),
                    card = entry.value
                )
            }
            .sortedBy { it.name }
    }

    /**
     * @param cardType Card family whose list-leading glyph should mirror category iconography.
     * @return Drawable resource id for manual list rows.
     */
    private fun getTypeIcon(cardType: CardTypeEnum): Int =
        when (cardType) {
            CardTypeEnum.ANIMAL -> R.drawable.ic_google_pets
            CardTypeEnum.CAR -> R.drawable.ic_google_directions_car
            CardTypeEnum.HOUSE -> R.drawable.ic_google_home
            CardTypeEnum.JOB -> R.drawable.ic_google_work
            CardTypeEnum.SPORT -> R.drawable.ic_google_fitness_center
            CardTypeEnum.LOVE -> R.drawable.ic_google_favorite
        }
}
