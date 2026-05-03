package com.example.eticketing.activities

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.eticketing.R
import com.example.eticketing.databinding.ActivityAdminBrowseBinding

class AdminBrowseActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminBrowseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBrowseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBackButton("Browse Destinasi")

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragmentBrowse) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavBrowse.setupWithNavController(navController)
    }
}