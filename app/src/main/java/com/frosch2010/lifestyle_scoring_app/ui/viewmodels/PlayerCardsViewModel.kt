package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.domain.model.CategoryPointsBreakdown
import com.frosch2010.lifestyle_scoring_app.domain.usecase.AddCardUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.AddCardUseCaseResult
import com.frosch2010.lifestyle_scoring_app.domain.usecase.CalculateCategoryPointsBreakdownUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.DeletePlayerUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetCarCardPointsUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetCardNameUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetLoveCardTypesUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetLoveCardsWithIconUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetLoveIconUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetPlayerUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.UpdatePlayerUseCase
import com.frosch2010.lifestyle_scoring_app.models.entities.CarCard
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.LoveTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.common.Event
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.CardDTO
import com.frosch2010.lifestyle_scoring_app.utils.ScanResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Coordinates player hand mutations, duplicate guards, manual follow-up dialogs, and category score breakdowns.
 *
 * @param addCardUseCase Domain-level duplicate check before mutating repository state.
 * @param getCardNameUseCase Localized titles for list rows and toasts.
 * @param getCarCardPointsUseCase Manual point ladders for car dialogs.
 * @param getLoveCardTypesUseCase Ordered labels aligned with love enum mapping.
 * @param getLoveCardsWithIconUseCase Icon-rich rows for love specialization picker.
 * @param getLoveIconUseCase Drawable id for love rows in the card list.
 * @param getPlayerUseCase Authoritative player snapshot including mutable card list reference.
 * @param updatePlayerUseCase Persists card/name mutations back to the session repository.
 * @param deletePlayerUseCase Removes the whole participant when invoked from overflow actions.
 * @param calculateCategoryPointsBreakdownUseCase Recomputes analytics after each successful add/remove.
 */
@HiltViewModel
class PlayerCardsViewModel @Inject constructor(
    private val addCardUseCase: AddCardUseCase,
    private val getCardNameUseCase: GetCardNameUseCase,
    private val getCarCardPointsUseCase: GetCarCardPointsUseCase,
    private val getLoveCardTypesUseCase: GetLoveCardTypesUseCase,
    private val getLoveCardsWithIconUseCase: GetLoveCardsWithIconUseCase,
    private val getLoveIconUseCase: GetLoveIconUseCase,
    private val getPlayerUseCase: GetPlayerUseCase,
    private val updatePlayerUseCase: UpdatePlayerUseCase,
    private val deletePlayerUseCase: DeletePlayerUseCase,
    private val calculateCategoryPointsBreakdownUseCase: CalculateCategoryPointsBreakdownUseCase
) : ViewModel() {

    /**
     * Combined toolbar, card grid, and analytics panel state for the player cards screen.
     *
     * @property playerName Current display name mirrored from the repository entity.
     * @property cards Adapter models reflecting the ordered hand.
     * @property categoryPointsBreakdown Latest scoring snapshot, or `null` before first successful load.
     * @property isEmptyStateVisible `true` when the player owns zero cards.
     */
    data class UiState(
        val playerName: String = "",
        val cards: List<CardDTO> = emptyList(),
        val categoryPointsBreakdown: CategoryPointsBreakdown? = null,
        val isEmptyStateVisible: Boolean = true
    )

    /**
     * One-shot UI work such as snack messages or modal flows requiring activity context.
     */
    sealed class UiEvent {
        /**
         * Shows a localized string resource (toast/snackbar) identified by [resId].
         *
         * @property resId Android string resource id such as [R.string.duplicate_card_message].
         */
        data class ShowMessage(val resId: Int) : UiEvent()

        /**
         * Opens the manual car point dialog before persisting a partially specified car card.
         *
         * @property card Car instance lacking final printed points.
         * @property choices Allowed increments from domain rules.
         */
        data class AskForManualCarPoints(val card: CarCard, val choices: List<Int>) : UiEvent()

        /**
         * Opens the love specialization dialog for generic love cards.
         *
         * @property card Love card awaiting a concrete [LoveTypeEnum].
         * @property options `(label, drawableResId)` tuples for dialog rows.
         */
        data class AskForManualLoveType(
            val card: ICard,
            val options: List<Pair<String, Int>>
        ) : UiEvent()
    }

    /** Roster index passed from [com.frosch2010.lifestyle_scoring_app.ui.util.IntentExtras.EXTRA_PLAYER_NUMBER]. */
    private var playerIndex = 0

    private val _playerName = MutableLiveData<String>()
    private val _playerCards = MutableLiveData<List<CardDTO>>()
    private val _categoryPointsBreakdown = MutableLiveData<CategoryPointsBreakdown>()
    private val _uiState = MutableLiveData(UiState())
    private val _events = MutableLiveData<Event<UiEvent>>()

    val playerCards: LiveData<List<CardDTO>> = _playerCards
    val playerName: LiveData<String> = _playerName
    val categoryPointsBreakdown: LiveData<CategoryPointsBreakdown> = _categoryPointsBreakdown
    val uiState: LiveData<UiState> = _uiState
    val events: LiveData<Event<UiEvent>> = _events

    /**
     * Recomputes [CategoryPointsBreakdown] from the repository hand and pushes an updated [UiState].
     */
    private fun refreshCategoryPointsBreakdown() {
        val player = getPlayerUseCase(playerIndex)
        _categoryPointsBreakdown.value = calculateCategoryPointsBreakdownUseCase(player.cards)
        updateUiState()
    }

    /**
     * Appends [card] to both the observable DTO list and the mutable repository entity, then refreshes scoring.
     *
     * @param card Domain card that already passed duplicate validation.
     */
    private fun addCard(card: ICard) {
        val currentCards = _playerCards.value?.toMutableList() ?: mutableListOf()
        when (card.cardType) {
            CardTypeEnum.CAR -> currentCards.add(
                CardDTO(CardTypeEnum.CAR, getCardNameUseCase(card), (card as CarCard).points)
            )
            CardTypeEnum.ANIMAL -> currentCards.add(CardDTO(CardTypeEnum.ANIMAL, getCardNameUseCase(card)))
            CardTypeEnum.SPORT -> currentCards.add(CardDTO(CardTypeEnum.SPORT, getCardNameUseCase(card)))
            CardTypeEnum.HOUSE -> currentCards.add(CardDTO(CardTypeEnum.HOUSE, getCardNameUseCase(card)))
            CardTypeEnum.JOB -> currentCards.add(CardDTO(CardTypeEnum.JOB, getCardNameUseCase(card)))
            CardTypeEnum.LOVE -> currentCards.add(
                CardDTO(
                    CardTypeEnum.LOVE,
                    getCardNameUseCase(card),
                    null,
                    getLoveIconUseCase(card as LoveCard)
                )
            )
        }
        _playerCards.value = currentCards
        val player = getPlayerUseCase(playerIndex)
        player.cards.add(card)
        updatePlayerUseCase(playerIndex, player)
        refreshCategoryPointsBreakdown()
        updateUiState()
    }

    /**
     * Hydrates DTO rows and scoring from the repository for the player at [playerIndex].
     *
     * @param playerIndex Stable roster slot identifying whose hand to display.
     */
    fun loadCardsForPlayer(playerIndex: Int) {
        val player = getPlayerUseCase(playerIndex)
        this.playerIndex = playerIndex
        _playerCards.value = listOf()
        player.cards.forEach {
            val currentCards = _playerCards.value?.toMutableList() ?: mutableListOf()
            when (it.cardType) {
                CardTypeEnum.CAR -> currentCards.add(
                    CardDTO(CardTypeEnum.CAR, getCardNameUseCase(it), (it as CarCard).points)
                )
                CardTypeEnum.ANIMAL -> currentCards.add(CardDTO(CardTypeEnum.ANIMAL, getCardNameUseCase(it)))
                CardTypeEnum.SPORT -> currentCards.add(CardDTO(CardTypeEnum.SPORT, getCardNameUseCase(it)))
                CardTypeEnum.HOUSE -> currentCards.add(CardDTO(CardTypeEnum.HOUSE, getCardNameUseCase(it)))
                CardTypeEnum.JOB -> currentCards.add(CardDTO(CardTypeEnum.JOB, getCardNameUseCase(it)))
                CardTypeEnum.LOVE -> currentCards.add(
                    CardDTO(
                        CardTypeEnum.LOVE,
                        getCardNameUseCase(it),
                        null,
                        getLoveIconUseCase(it as LoveCard)
                    )
                )
            }
            _playerCards.value = currentCards
        }
        refreshCategoryPointsBreakdown()
        updateUiState()
    }

    /**
     * Validates scan/manual results against the live hand, surfacing a message on duplicates.
     *
     * @param result Payload from capture flows; ignored when [ScanResult.cards] is null.
     */
    fun gotScanResult(result: ScanResult) {
        val card = result.cards ?: return
        val existing = getPlayerUseCase(playerIndex).cards
        when (addCardUseCase(card, existing)) {
            AddCardUseCaseResult.Duplicate -> _events.value = Event(UiEvent.ShowMessage(R.string.duplicate_card_message))
            AddCardUseCaseResult.Ready -> addCard(card)
        }
    }

    /**
     * Routes manual catalog selections through the same disambiguation dialogs used after OCR.
     *
     * @param card Card chosen from the manual picker; may still require points or love subtype.
     */
    fun onManualCardSelected(card: ICard) {
        when (card.cardType) {
            CardTypeEnum.CAR -> _events.value = Event(
                UiEvent.AskForManualCarPoints(
                    card = card as CarCard,
                    choices = getCarCardPointsUseCase(card)
                )
            )
            CardTypeEnum.LOVE -> _events.value = Event(
                UiEvent.AskForManualLoveType(
                    card = card,
                    options = getLoveCardsWithIconUseCase()
                )
            )
            else -> gotScanResult(ScanResult(success = true, cards = card))
        }
    }

    /**
     * Completes a manual car flow by wrapping the chosen points into a new [CarCard] before [gotScanResult].
     *
     * @param card Subtype context for the replacement instance.
     * @param selectedPoints User-selected face value.
     */
    fun onManualCarPointsSelected(card: CarCard, selectedPoints: Int) {
        gotScanResult(
            ScanResult(
                success = true,
                cards = CarCard(card.cardType, card.carType, selectedPoints)
            )
        )
    }

    /**
     * Maps dialog label selection to [LoveTypeEnum] using the same ordering as [getLoveCardTypesUseCase].
     *
     * @param card Baseline love card (typically UNKNOWN subtype).
     * @param selectedLabel Localized label chosen in the dialog.
     */
    fun onManualLoveTypeSelected(card: ICard, selectedLabel: String) {
        val labels = getLoveCardTypesUseCase()
        val index = labels.indexOf(selectedLabel)
        val loveType = if (index >= 0) {
            LoveTypeEnum.entries[index + 1]
        } else {
            LoveTypeEnum.UNKNOWN
        }
        gotScanResult(ScanResult(success = true, cards = LoveCard(card.cardType, loveType)))
    }

    /**
     * @param card Car instance whose ladder should be shown.
     * @return Delegated point options for UI pickers.
     */
    fun getCarCardPoints(card: ICard): List<Int> {
        return getCarCardPointsUseCase(card)
    }

    /**
     * @return Ordered love subtype labels for simple list dialogs.
     */
    fun getLoveCardTypes(): List<String> {
        return getLoveCardTypesUseCase()
    }

    /**
     * @return Label/icon tuples for icon list dialogs.
     */
    fun getLoveCardsWithIcon(): List<Pair<String, Int>> {
        return getLoveCardsWithIconUseCase()
    }

    /**
     * Refreshes the toolbar title after returning from rename flows.
     *
     * @param playerIndex Roster slot whose name should be re-read.
     */
    fun updatePlayerName(playerIndex: Int) {
        _playerName.value = getPlayerUseCase(playerIndex).name
        updateUiState()
    }

    /**
     * Persists a new display name for the active player index.
     *
     * @param newName Trimmed name from the rename dialog.
     */
    fun editPlayerName(newName: String) {
        val player = getPlayerUseCase(playerIndex)
        player.name = newName
        updatePlayerUseCase(playerIndex, player)
        _playerName.value = newName
        updateUiState()
    }

    /**
     * Removes the entire player row; activity typically finishes afterward.
     *
     * @param index Roster slot to delete.
     */
    fun deletePlayerAt(index: Int) {
        deletePlayerUseCase(index)
    }

    /**
     * Removes a single card by UI index and mirrors the mutation into the repository entity.
     *
     * @param cardIndex Adapter position aligned with the player's `cards` list order.
     */
    fun deleteCard(cardIndex: Int) {
        val player = getPlayerUseCase(playerIndex)
        val currentCards = _playerCards.value?.toMutableList() ?: mutableListOf()
        currentCards.removeAt(cardIndex)
        _playerCards.value = currentCards
        player.cards.removeAt(cardIndex)
        updatePlayerUseCase(playerIndex, player)
        refreshCategoryPointsBreakdown()
        updateUiState()
    }

    /**
     * Flattens granular LiveData fields into the single [UiState] exposed to collectors.
     */
    private fun updateUiState() {
        val cards = _playerCards.value.orEmpty()
        _uiState.value = UiState(
            playerName = _playerName.value.orEmpty(),
            cards = cards,
            categoryPointsBreakdown = _categoryPointsBreakdown.value,
            isEmptyStateVisible = cards.isEmpty()
        )
    }
}
