package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.ActivityMainBinding
import com.frosch2010.lifestyle_scoring_app.ui.adapters.PlayerAdapter
import com.frosch2010.lifestyle_scoring_app.ui.decorations.AdaptiveSpacingItemDecoration
import com.frosch2010.lifestyle_scoring_app.ui.dialogs.PlayerNameDialog
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.MainViewModel
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.PlayerDTO
import dagger.hilt.android.AndroidEntryPoint

/**
 * This activity is used to display the list of players and to add new players.
 * @author Jens Münker
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PlayerAdapter.OnPlayerClickedListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val adapter = PlayerAdapter(listOf(), this, this, viewModel)
        binding.recView.layoutManager = LinearLayoutManager(this)
        binding.recView.adapter = adapter
        binding.recView.addItemDecoration(AdaptiveSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_small), true))

        viewModel.players.observe(this) { itemList ->
            adapter.updateData(itemList)

            if(itemList.isEmpty()) {
                binding.txtNoPlayers.visibility = android.view.View.VISIBLE
            } else {
                binding.txtNoPlayers.visibility = android.view.View.GONE
            }
        }

        binding.fabAddPlayer.setOnClickListener {
            val playerNameDialog = PlayerNameDialog(this, object : PlayerNameDialog.OnNameEnteredListener {
                override fun onNameEntered(playerName: String) {
                    viewModel.addPlayer(PlayerDTO(playerName, 0))
                }
            })
            playerNameDialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.recalculatePlayerPoints()
    }

    override fun onPlayerClicked(index: Int) {
        val intent = Intent(this, PlayerCardsActivity::class.java)
        intent.putExtra("player_number", index)
        startActivity(intent)
    }
}