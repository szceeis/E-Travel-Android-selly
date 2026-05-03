package com.example.eticketing.activities

import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    fun setupBackButton(title: String) {
        supportActionBar?.apply {
            show()
            setDisplayHomeAsUpEnabled(true)
            this.title = title
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}