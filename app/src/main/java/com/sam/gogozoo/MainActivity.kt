package com.sam.gogozoo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sam.gogozoo.databinding.ActivityMainBinding
import com.sam.gogozoo.ext.getVmFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val viewModel by viewModels<MainViewModel> { getVmFactory() }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        viewModel
        val navController = Navigation.findNavController(this, R.id.myNavHostFragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavView)
        bottomNavigationView.setupWithNavController(navController)
        changeTitleAndPage()
    }

    private fun changeTitleAndPage() {

        val navController = Navigation.findNavController(this, R.id.myNavHostFragment)
        bottomNavView.setOnNavigationItemSelectedListener { it ->
            when (it.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.listFragment -> {
                    navController.navigate(R.id.listFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.scheduleFragment -> {
                    navController.navigate(R.id.scheduleFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    navController.navigate(R.id.personFragment)
                    return@setOnNavigationItemSelectedListener true
                    }
                }
            }
        }
    }


