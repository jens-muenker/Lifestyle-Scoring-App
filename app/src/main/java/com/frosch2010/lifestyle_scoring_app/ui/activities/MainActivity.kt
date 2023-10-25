package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.ActivityMainBinding
import com.frosch2010.lifestyle_scoring_app.ui.adapters.PlayerAdapter
import com.frosch2010.lifestyle_scoring_app.ui.adapters.PlayerCardsAdapter
import com.frosch2010.lifestyle_scoring_app.ui.dialogs.PlayerNameDialog
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.MainViewModel
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.PlayerCardsViewModel
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.PlayerDTO
import dagger.hilt.android.AndroidEntryPoint

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

        val adapter = PlayerAdapter(listOf(), this)
        binding.recView.layoutManager = LinearLayoutManager(this)
        binding.recView.adapter = adapter

        viewModel.players.observe(this) { itemList ->
            adapter.updateData(itemList)
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