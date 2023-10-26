package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.ActivityPlayerCardsBinding
import com.frosch2010.lifestyle_scoring_app.databinding.ActivityScanCardBinding
import com.frosch2010.lifestyle_scoring_app.ui.adapters.PlayerCardsAdapter
import com.frosch2010.lifestyle_scoring_app.ui.dialogs.PlayerNameDialog
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.PlayerCardsViewModel
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.ScanCardViewModel
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.PlayerDTO
import com.frosch2010.lifestyle_scoring_app.utils.ScanResult
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayerCardsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerCardsBinding

    private lateinit var viewModel: PlayerCardsViewModel

    private lateinit var handleScanCard: ActivityResultLauncher<Intent>

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_player, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_name -> {
                val playerNameDialog = PlayerNameDialog(this, object : PlayerNameDialog.OnNameEnteredListener {
                    override fun onNameEntered(playerName: String) {
                        viewModel.editPlayerName(playerName)
                    }
                })
                playerNameDialog.show()
                true
            }
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[PlayerCardsViewModel::class.java]

        binding = ActivityPlayerCardsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setSupportActionBar(binding.toolbar)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val playerNumber = intent.getIntExtra("player_number", -1)
        if (playerNumber != -1) {
            viewModel.loadCardsForPlayer(playerNumber)
            viewModel.updatePlayerName(playerNumber)
        } else {
            // TODO: Error handling
        }

        initHandler()

        val adapter = PlayerCardsAdapter(listOf(), this)
        binding.recView.layoutManager = LinearLayoutManager(this)
        binding.recView.adapter = adapter

        viewModel.playerCards.observe(this) { itemList ->
            adapter.updateData(itemList)

            if(itemList.isEmpty()){
                binding.txtNoCards.visibility = VISIBLE
            }else{
                binding.txtNoCards.visibility = GONE
            }
        }

        binding.btnScan.setOnClickListener {
            val intent = Intent(this, ScanCardActivity::class.java)
            handleScanCard.launch(intent)
        }
    }

    private fun initHandler() {
        handleScanCard = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.data == null){
                return@registerForActivityResult
            }
            if(result.resultCode == Activity.RESULT_OK){

                val scanResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data!!.getSerializableExtra("scan_result", ScanResult::class.java)
                } else {
                    result.data!!.getSerializableExtra("scan_result") as? ScanResult
                }

                if(scanResult == null || !scanResult.success){
                    Toast.makeText(this, "Ein Fehler ist beim Scan aufgetreten. Versuche es nochmal.", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }

                viewModel.gotScanResult(scanResult)
            }else{
                Toast.makeText(this, "Ein Fehler ist beim Scan aufgetreten. Versuche es nochmal.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}