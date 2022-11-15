package com.example.mobile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mobile.R
import com.example.mobile.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        setupNav()
    }

    private fun setupNav() {
        val navController =
            supportFragmentManager.findFragmentById(R.id.fragment_container)?.findNavController()
        navController?.let {
            binding.bottomNavigationView.setupWithNavController(it)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment -> hideBottomNavigation()
                R.id.signUpFragment -> hideBottomNavigation()
                else -> showBottomNavigation()
            }
        }
    }

    private fun showBottomNavigation() {
        binding.bottomNavigationView.isVisible = true
    }

    private fun hideBottomNavigation() {
        binding.bottomNavigationView.isVisible = false
    }

}




