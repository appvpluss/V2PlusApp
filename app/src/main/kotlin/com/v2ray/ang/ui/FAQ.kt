package com.v2ray.ang.ui
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.v2ray.ang.R

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