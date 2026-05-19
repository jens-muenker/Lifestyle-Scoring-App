package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetCarCardPointsUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetLoveCardTypesUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetLoveCardsWithIconUseCase
import com.frosch2010.lifestyle_scoring_app.models.entities.CarCard
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.LoveTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.common.Event
import com.frosch2010.lifestyle_scoring_app.utils.ScanResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Post-processes camera OCR output: optional car point picking, love subtype disambiguation, then result delivery.
 *
 * @param getCarCardPointsUseCase Supplies manual score ladders for recognized car cards.
 * @param getLoveCardTypesUseCase Ordered labels aligned with [LoveTypeEnum] indices used after UNKNOWN.
 * @param getLoveCardsWithIconUseCase Rich rows for the love subtype picker dialog.
 */
@HiltViewModel
class ScanCardViewModel @Inject constructor(
    private val getCarCardPointsUseCase: GetCarCardPointsUseCase,
    private val getLoveCardTypesUseCase: GetLoveCardTypesUseCase,
    private val getLoveCardsWithIconUseCase: GetLoveCardsWithIconUseCase
) : ViewModel() {

    /**
     * One-shot pipeline steps consumed by [com.frosch2010.lifestyle_scoring_app.ui.activities.ScanCardActivity].
     */
    sealed class UiEvent {
        /**
         * Recognition failed or produced no card; still carries the raw [ScanResult] for logging or UX copy.
         *
         * @property result Original scan payload including success flag and nullable card.
         */
        data class ShowNoCardRecognized(val result: ScanResult) : UiEvent()

        /**
         * Prompts the user to choose printed points for a car card before finishing the activity.
         *
         * @property card Recognized car instance possibly lacking final point selection.
         * @property success Whether upstream OCR considered the detection successful.
         * @property choices Allowed manual increments from domain rules.
         */
        data class AskForCarPoints(val card: CarCard, val success: Boolean, val choices: List<Int>) : UiEvent()

        /**
         * Prompts the user to specialize a generic love card using labeled icon rows.
         *
         * @property card Love card currently typed as [LoveTypeEnum.UNKNOWN] or needing confirmation.
         * @property success Whether upstream OCR considered the detection successful.
         * @property options `(label, drawableResId)` tuples for dialog rows.
         */
        data class AskForLoveType(
            val card: ICard,
            val success: Boolean,
            val options: List<Pair<String, Int>>
        ) : UiEvent()

        /**
         * Terminal event returning the finalized [ScanResult] to the caller via `setResult`.
         *
         * @property result Payload ready for serialization on the intent back-stack.
         */
        data class DeliverResult(val result: ScanResult) : UiEvent()
    }

    private val _events = MutableLiveData<Event<UiEvent>>()
    val events: LiveData<Event<UiEvent>> = _events

    /**
     * Entry point from the camera pipeline once ML Kit and fuzzy matching have produced a [ScanResult].
     *
     * @param result Raw recognition outcome; failures still trigger a terminal delivery for consistent activity finish.
     */
    fun onScanCompleted(result: ScanResult) {
        val card = result.cards
        if (!result.success || card == null) {
            _events.value = Event(UiEvent.ShowNoCardRecognized(result))
            _events.value = Event(UiEvent.DeliverResult(result))
            return
        }
        onCardSelected(card, true)
    }

    /**
     * Routes a concrete card either to follow-up dialogs (car/love) or directly to delivery.
     *
     * @param card Non-null recognized instance.
     * @param success Propagated success bit from the scan pipeline for downstream messaging.
     */
    fun onCardSelected(card: ICard, success: Boolean = true) {
        when (card.cardType) {
            CardTypeEnum.CAR -> {
                _events.value = Event(
                    UiEvent.AskForCarPoints(
                        card = card as CarCard,
                        success = success,
                        choices = getCarCardPointsUseCase(card)
                    )
                )
            }
            CardTypeEnum.LOVE -> {
                _events.value = Event(
                    UiEvent.AskForLoveType(
                        card = card,
                        success = success,
                        options = getLoveCardsWithIconUseCase()
                    )
                )
            }
            else -> _events.value = Event(UiEvent.DeliverResult(ScanResult(success, card)))
        }
    }

    /**
     * Merges the user-selected car points into a new [CarCard] and emits [UiEvent.DeliverResult].
     *
     * @param card Baseline car instance whose subtype is preserved.
     * @param points User-selected face value from [UiEvent.AskForCarPoints.choices].
     * @param success Success flag propagated to the caller activity.
     */
    fun onCarPointsSelected(card: CarCard, points: Int, success: Boolean) {
        _events.value = Event(
            UiEvent.DeliverResult(
                ScanResult(
                    success = success,
                    cards = CarCard(card.cardType, card.carType, points)
                )
            )
        )
    }

    /**
     * Maps the chosen love label back to [LoveTypeEnum] using the same ordering as [getLoveCardTypesUseCase].
     *
     * @param card Placeholder love card whose [com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum] is LOVE.
     * @param selectedLabel Localized string matching one of the dialog options.
     * @param success Success bit forwarded to the caller.
     */
    fun onLoveTypeSelected(card: ICard, selectedLabel: String, success: Boolean) {
        val labels = getLoveCardTypesUseCase()
        val index = labels.indexOf(selectedLabel)
        val loveType = if (index >= 0) {
            LoveTypeEnum.entries[index + 1]
        } else {
            LoveTypeEnum.UNKNOWN
        }
        _events.value = Event(
            UiEvent.DeliverResult(
                ScanResult(success = success, cards = LoveCard(card.cardType, loveType))
            )
        )
    }
}
