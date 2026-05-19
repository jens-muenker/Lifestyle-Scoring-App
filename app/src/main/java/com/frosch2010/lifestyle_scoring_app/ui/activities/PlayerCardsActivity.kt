package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.withClip
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.ActivityPlayerCardsBinding
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.ui.adapters.PlayerCardsAdapter
import com.frosch2010.lifestyle_scoring_app.ui.adapters.PointsSummaryAdapter
import com.frosch2010.lifestyle_scoring_app.ui.decorations.AdaptiveSpacingItemDecoration
import com.frosch2010.lifestyle_scoring_app.ui.dialogs.DeletePlayerCardDialog
import com.frosch2010.lifestyle_scoring_app.ui.dialogs.DeletePlayerDialog
import com.frosch2010.lifestyle_scoring_app.ui.dialogs.ListDialog
import com.frosch2010.lifestyle_scoring_app.ui.dialogs.ListDialogWithIcons
import com.frosch2010.lifestyle_scoring_app.ui.dialogs.PlayerNameDialog
import com.frosch2010.lifestyle_scoring_app.ui.util.EdgeToEdgeTopBarController
import com.frosch2010.lifestyle_scoring_app.ui.util.IntentExtras
import com.frosch2010.lifestyle_scoring_app.ui.util.getSerializableExtraCompat
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.PlayerCardsViewModel
import com.frosch2010.lifestyle_scoring_app.utils.ScanResult
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint

/**
 * Hands-on screen for one player: card list with swipe-delete, scan/manual capture, category breakdown, and rename/delete.
 *
 * The roster index is supplied through [IntentExtras.EXTRA_PLAYER_NUMBER].
 */
@AndroidEntryPoint
class PlayerCardsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerCardsBinding
    private lateinit var viewModel: PlayerCardsViewModel

    /** Contract for launching [ScanCardActivity] and receiving a [ScanResult]. */
    private lateinit var handleScanCard: ActivityResultLauncher<Intent>

    /** Contract for launching [ManualAddCardActivity] with exclusion extras. */
    private lateinit var handleManualAddCard: ActivityResultLauncher<Intent>

    /** Mirrors vertical scroll state for coordinated top bar elevation. */
    private var isTopBarScrolledState = false

    /** User-toggleable expansion for the points breakdown section inside [summaryAdapter]. */
    private var isBreakdownExpanded = false

    /** Repository-aligned player index parsed from the launching intent. */
    private var playerListIndex: Int = -1

    private lateinit var topBarController: EdgeToEdgeTopBarController

    /** Bottom inset reused when positioning FABs and empty states after window insets. */
    private var lastSystemBottomInset: Int = 0

    private lateinit var cardsAdapter: PlayerCardsAdapter
    private lateinit var summaryAdapter: PointsSummaryAdapter

    /**
     * @param menu Destination menu inflated with rename/delete actions.
     * @return Always `true` because this screen always exposes toolbar actions.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_player, menu)
        return true
    }

    /**
     * @param item Selected toolbar entry (rename vs delete entire player).
     * @return `true` when the event was consumed; otherwise defers to the superclass chain.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_name -> {
                val currentName = viewModel.playerName.value.orEmpty()
                PlayerNameDialog(
                    context = this,
                    listener = object : PlayerNameDialog.OnNameEnteredListener {
                        override fun onNameEntered(playerName: String) {
                            viewModel.editPlayerName(playerName)
                        }
                    },
                    dialogTitleRes = R.string.edit_player_name,
                    initialName = currentName,
                    onDismiss = { viewModel.updatePlayerName(playerListIndex) }
                ).show()
                true
            }
            R.id.menu_delete_player -> {
                DeletePlayerDialog(this, object : DeletePlayerDialog.IDeletePlayerDialogCallback {
                    override fun onDeleteConfirmed(playerIndex: Int) {
                        viewModel.deletePlayerAt(playerIndex)
                        finish()
                    }
                }).show(
                    getString(R.string.delete_player),
                    getString(R.string.message_delete_player),
                    playerListIndex
                )
                true
            }
            else -> false
        }
    }

    /**
     * Parses [IntentExtras.EXTRA_PLAYER_NUMBER], wires concat adapters, swipe delete, FAB targets, and observers.
     *
     * @param savedInstanceState Unused; player index is always re-read from the intent extras.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[PlayerCardsViewModel::class.java]

        binding = ActivityPlayerCardsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val initialActionsMarginStart = (binding.layoutActions.layoutParams as ViewGroup.MarginLayoutParams).marginStart
        val initialActionsMarginEnd = (binding.layoutActions.layoutParams as ViewGroup.MarginLayoutParams).marginEnd
        val initialActionsMarginBottom = (binding.layoutActions.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin

        topBarController = EdgeToEdgeTopBarController(
            activity = this,
            rootForInsets = binding.root,
            toolbarForInsets = binding.toolbar,
            topBarBackgroundView = binding.toolbar,
            topBarDivider = binding.topBarDivider,
            scrollPaddingView = binding.recView,
            isDarkMode = ::isDarkMode,
            isScrolled = { binding.recView.canScrollVertically(-1) },
            extraInsetsApplier = { insets ->
                val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                lastSystemBottomInset = bars.bottom

                (binding.layoutActions.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    marginStart = initialActionsMarginStart + bars.left
                    marginEnd = initialActionsMarginEnd + bars.right
                    bottomMargin = initialActionsMarginBottom + bars.bottom
                    binding.layoutActions.layoutParams = this
                }
                updateRecyclerBottomPadding()
            }
        )
        topBarController.setup()
        topBarController.updateTopBarScrollState(isTopBarScrolledState)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setSupportActionBar(binding.toolbar)

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnToggleBreakdown.setOnClickListener {
            isBreakdownExpanded = !isBreakdownExpanded
            updateBreakdownVisibility()
        }
        updateBreakdownVisibility()

        val playerNumber = intent.getIntExtra(IntentExtras.EXTRA_PLAYER_NUMBER, -1)
        if (playerNumber != -1) {
            playerListIndex = playerNumber
            viewModel.loadCardsForPlayer(playerNumber)
            viewModel.updatePlayerName(playerNumber)
        } else {
            Toast.makeText(this, getString(R.string.scan_error_message), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initHandler()

        summaryAdapter = PointsSummaryAdapter {
            isBreakdownExpanded = !isBreakdownExpanded
            updateSummaryAdapter()
        }
        cardsAdapter = PlayerCardsAdapter(this, viewModel) { summaryAdapter.itemCount }
        val adapter = ConcatAdapter(summaryAdapter, cardsAdapter)
        val layoutManager = LinearLayoutManager(this)

        binding.recView.layoutManager = layoutManager
        binding.recView.setHasFixedSize(true)
        binding.recView.adapter = adapter
        binding.recView.addItemDecoration(AdaptiveSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_small), true))
        attachSwipeToDelete(cardsAdapter)
        binding.recView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                updateTopBarScrollState()
            }
        })
        binding.recView.post { updateTopBarScrollState() }

        viewModel.uiState.observe(this) { uiState ->
            cardsAdapter.updateData(uiState.cards)
            if (uiState.cards.isNotEmpty()) {
                binding.recView.scrollToPosition(uiState.cards.size - 1)
            }

            if (uiState.isEmptyStateVisible) {
                binding.layoutEmptyCardsState.visibility = VISIBLE
                binding.recView.visibility = GONE
            } else {
                binding.layoutEmptyCardsState.visibility = GONE
                binding.recView.visibility = VISIBLE
            }
            uiState.categoryPointsBreakdown?.let { breakdown ->
                val totalText = resources.getQuantityString(
                    R.plurals.player_cards_total_points,
                    breakdown.totalPoints,
                    breakdown.totalPoints
                )
                summaryAdapter.update(totalText, breakdown.entries, isBreakdownExpanded)
            }
        }
        viewModel.events.observe(this) { event ->
            when (val uiEvent = event.consume()) {
                is PlayerCardsViewModel.UiEvent.ShowMessage ->
                    Toast.makeText(this, getString(uiEvent.resId), Toast.LENGTH_SHORT).show()
                is PlayerCardsViewModel.UiEvent.AskForManualCarPoints -> {
                    ListDialog(
                        this,
                        getString(R.string.how_many_points),
                        uiEvent.choices.map { it.toString() },
                        object : ListDialog.OnItemSelectedListener {
                            override fun onItemSelected(item: String) {
                                viewModel.onManualCarPointsSelected(uiEvent.card, item.toInt())
                            }
                        }
                    ).show()
                }
                is PlayerCardsViewModel.UiEvent.AskForManualLoveType -> {
                    ListDialogWithIcons(
                        this,
                        getString(R.string.what_type_of_love),
                        uiEvent.options,
                        object : ListDialogWithIcons.OnItemSelectedListener {
                            override fun onItemSelected(item: String) {
                                viewModel.onManualLoveTypeSelected(uiEvent.card, item)
                            }
                        }
                    ).show()
                }
                null -> Unit
            }
        }

        binding.btnScan.setOnClickListener {
            val intent = Intent(this, ScanCardActivity::class.java)
            handleScanCard.launch(intent)
        }

        binding.btnManualAdd.setOnClickListener {
            val existingCardNames = ArrayList(
                viewModel.playerCards.value
                    .orEmpty()
                    .map { it.name }
            )
            val intent = Intent(this, ManualAddCardActivity::class.java).apply {
                putStringArrayListExtra(ManualAddCardActivity.EXTRA_EXISTING_CARD_NAMES, existingCardNames)
            }
            handleManualAddCard.launch(intent)
        }

        binding.layoutActions.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateRecyclerBottomPadding()
        }
    }

    /** Registers activity-result handlers for scan and manual add flows. */
    private fun initHandler() {
        handleScanCard = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.data == null){
                return@registerForActivityResult
            }
            if(result.resultCode == RESULT_OK){
                val scanResult = result.data?.getSerializableExtraCompat<ScanResult>(IntentExtras.EXTRA_SCAN_RESULT)

                if(scanResult == null || !scanResult.success){
                    Toast.makeText(this, getString(R.string.scan_error_message), Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }

                viewModel.gotScanResult(scanResult)
            }else{
                Toast.makeText(this, getString(R.string.scan_error_message), Toast.LENGTH_SHORT).show()
            }
        }

        handleManualAddCard = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode != RESULT_OK || result.data == null) {
                return@registerForActivityResult
            }

            val card = result.data?.getSerializableExtraCompat<ICard>(ManualAddCardActivity.EXTRA_SELECTED_CARD)
                ?: return@registerForActivityResult

            viewModel.onManualCardSelected(card)
        }
    }

    /** Syncs top-bar visual state with current scroll position. */
    private fun updateTopBarScrollState() {
        val shouldUseScrolledStyle = binding.recView.canScrollVertically(-1)
        if (isTopBarScrolledState != shouldUseScrolledStyle) {
            isTopBarScrolledState = shouldUseScrolledStyle
            topBarController.updateTopBarScrollState(isTopBarScrolledState)
        }
    }

    /**
     * Returns whether system configuration is currently in night mode.
     *
     * @return `true` when [Configuration.UI_MODE_NIGHT_YES] is active.
     */
    private fun isDarkMode(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }

    /** Applies dynamic bottom padding so list content stays visible above action buttons. */
    private fun updateRecyclerBottomPadding() {
        val baseBottomPadding = resources.getDimensionPixelSize(R.dimen.player_cards_list_bottom_padding)
        val actionsHeight = binding.layoutActions.height
        val targetBottom = maxOf(baseBottomPadding, actionsHeight + lastSystemBottomInset + resources.getDimensionPixelSize(R.dimen.spacing_small))
        binding.recView.setPadding(
            binding.recView.paddingLeft,
            binding.recView.paddingTop,
            binding.recView.paddingRight,
            targetBottom
        )
    }

    /**
     * Configures swipe-to-delete for card rows, excluding the summary header row.
     *
     * @param adapter Adapter receiving delete callbacks after confirmation dialogs.
     */
    private fun attachSwipeToDelete(adapter: PlayerCardsAdapter) {
        val swipeLabelTextSize = resources.getDimension(R.dimen.swipe_action_text_size)
        val swipeCornerRadius = resources.getDimension(R.dimen.swipe_action_corner_radius)
        val swipeVerticalInset = resources.getDimension(R.dimen.swipe_action_vertical_inset)

        val swipeLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOnErrorContainer)
            textSize = swipeLabelTextSize
            textAlign = Paint.Align.LEFT
        }
        val swipeBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            /**
             * Never swipe the points summary row. Do **not** use [RecyclerView.ViewHolder.bindingAdapterPosition]
             * here: with [ConcatAdapter] it is often the **local** index in the sub-adapter (first card = 0),
             * so comparing to [summaryAdapter.itemCount] would wrongly block every card row.
             *
             * @param recyclerView Host list controlled by this [ItemTouchHelper].
             * @param viewHolder Candidate row for swipe gesture evaluation.
             * @return Swipe direction flags, or `0` when the row must not be swiped.
             */
            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                if (viewHolder is PointsSummaryAdapter.ViewHolder) {
                    return 0
                }
                return super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                // Avoid rare stuck “red delete strip” / offset after interrupted swipes.
                viewHolder.itemView.translationX = 0f
                viewHolder.itemView.translationY = 0f
                viewHolder.itemView.alpha = 1f
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Global index in the [ConcatAdapter] list (summary + cards).
                val position = viewHolder.absoluteAdapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return
                }

                val cardIndex = position - summaryAdapter.itemCount
                if (cardIndex < 0) {
                    binding.recView.adapter?.notifyItemChanged(position)
                    return
                }

                val deletePlayerCardDialog = DeletePlayerCardDialog(
                    this@PlayerCardsActivity,
                    adapter
                )
                deletePlayerCardDialog.show(
                    getString(R.string.delete_card),
                    getString(R.string.message_delete_card),
                    cardIndex,
                ) {
                    // After dialog closes: ItemTouchHelper has released the row — safe to rebind.
                    binding.recView.post {
                        val rvAdapter = binding.recView.adapter ?: return@post
                        if (position < rvAdapter.itemCount) {
                            rvAdapter.notifyItemChanged(position)
                        }
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (dX < 0f) {
                    val itemView = viewHolder.itemView
                    swipeBackgroundPaint.color =
                        MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorErrorContainer)
                    swipeLabelPaint.color =
                        MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOnErrorContainer)

                    val background = RectF(
                        itemView.right + dX,
                        itemView.top + swipeVerticalInset,
                        itemView.right.toFloat(),
                        itemView.bottom - swipeVerticalInset
                    )
                    c.drawRoundRect(background, swipeCornerRadius, swipeCornerRadius, swipeBackgroundPaint)
                    drawSwipeIconAndLabel(
                        c = c,
                        background = background,
                        text = getString(R.string.delete),
                        iconRes = R.drawable.ic_google_delete_outline,
                        textColor = swipeLabelPaint.color,
                        labelPaint = swipeLabelPaint,
                        alignToEnd = true
                    )
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.recView)
    }

    /**
     * Draws swipe background, delete icon, and label inside swipe area.
     *
     * @param c Canvas used by [ItemTouchHelper] for row decorations.
     * @param background Rounded rectangle representing the swipe action bounds.
     * @param text Localized action label, usually delete text.
     * @param iconRes Drawable resource id rendered next to [text].
     * @param textColor Foreground color for icon and label.
     * @param labelPaint Configured paint reused for text measurements and rendering.
     * @param alignToEnd `true` to right-align content inside [background].
     */
    private fun drawSwipeIconAndLabel(
        c: Canvas,
        background: RectF,
        text: String,
        iconRes: Int,
        textColor: Int,
        labelPaint: Paint,
        alignToEnd: Boolean
    ) {
        val iconSize = resources.getDimensionPixelSize(R.dimen.swipe_action_icon_size)
        val horizontalPadding = resources.getDimension(R.dimen.spacing_small)
        val gapBetweenIconAndText = resources.getDimension(R.dimen.swipe_action_label_gap)

        labelPaint.color = textColor

        val textWidth = labelPaint.measureText(text)
        val totalWidth = iconSize + gapBetweenIconAndText + textWidth
        val maxLeft = (background.right - horizontalPadding - totalWidth).coerceAtLeast(background.left + horizontalPadding)
        val iconLeft = if (alignToEnd) maxLeft else background.left + horizontalPadding

        val centerY = (background.top + background.bottom) / 2f
        val iconTop = centerY - iconSize / 2f

        c.withClip(background) {
            val drawable = ContextCompat.getDrawable(this@PlayerCardsActivity, iconRes)?.mutate()
            drawable?.setTint(textColor)
            drawable?.setBounds(
                iconLeft.toInt(),
                iconTop.toInt(),
                (iconLeft + iconSize).toInt(),
                (iconTop + iconSize).toInt()
            )
            drawable?.draw(this)

            val textStartX = iconLeft + iconSize + gapBetweenIconAndText
            val textY = centerY - ((labelPaint.descent() + labelPaint.ascent()) / 2f)
            drawText(text, textStartX, textY, labelPaint)
        }
    }

    /** Applies current expand/collapse state to summary adapter rows. */
    private fun updateBreakdownVisibility() {
        updateSummaryAdapter()
    }

    /** Rebuilds summary adapter content from latest breakdown values. */
    private fun updateSummaryAdapter() {
        val breakdown = viewModel.categoryPointsBreakdown.value ?: return
        val totalText = resources.getQuantityString(
            R.plurals.player_cards_total_points,
            breakdown.totalPoints,
            breakdown.totalPoints
        )
        summaryAdapter.update(totalText, breakdown.entries, isBreakdownExpanded)
    }
}
