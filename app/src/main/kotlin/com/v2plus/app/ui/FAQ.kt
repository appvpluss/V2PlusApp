package com.v2plus.app.ui
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.v2plus.com.v2plus.app.R

class FAQ : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)
        val btnClose = findViewById<Button>(R.id.btn_close)
        btnClose.setOnClickListener {
            finish()
        }

    }
}