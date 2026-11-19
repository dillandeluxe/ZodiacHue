package com.android.zodiachue

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class DeveloperPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer_page)

        findViewById<ImageButton>(R.id.btn_back_developers).setOnClickListener {
            finish()
        }
    }
}
