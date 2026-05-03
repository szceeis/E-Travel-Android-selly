package com.example.eticketing

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.eticketing.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val role = getSharedPreferences("session", Context.MODE_PRIVATE)
            .getString("userRole", "user")

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        when (role) {
            "admin" -> {
                navController.setGraph(R.navigation.nav_graph_admin)
                binding.bottomNav.inflateMenu(R.menu.bottom_nav_admin)
            }
            "pengelola" -> {
                navController.setGraph(R.navigation.nav_graph_pengelola)
                binding.bottomNav.inflateMenu(R.menu.bottom_nav_pengelola)
            }
            else -> {
                navController.setGraph(R.navigation.nav_graph_user)
                binding.bottomNav.inflateMenu(R.menu.bottom_nav_user)
            }
        }

        binding.bottomNav.setupWithNavController(navController)
    }
}