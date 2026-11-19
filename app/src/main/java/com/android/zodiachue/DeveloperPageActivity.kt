package com.android.zodiachue

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DeveloperPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer_page)

        val dev2Name = findViewById<TextView>(R.id.txt_developer_2_name)
        if (dev2Name.text.isNullOrBlank()) {
            dev2Name.visibility = View.GONE
        }

        findViewById<ImageButton>(R.id.btn_back_developers).setOnClickListener {
            finish()
        }
    }
}
