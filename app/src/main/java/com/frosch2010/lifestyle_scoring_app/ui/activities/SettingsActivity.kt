package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.frosch2010.lifestyle_scoring_app.R
import io.noties.markwon.Markwon


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val markwon = Markwon.builder(this)
            .build()

        val markdownText = getString(R.string.privacy_policy)

        val textView = findViewById<TextView>(R.id.txt_privacy_policy)

        markwon.setMarkdown(textView, markdownText)
    }
}