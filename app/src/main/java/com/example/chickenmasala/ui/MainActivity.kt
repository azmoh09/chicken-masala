package com.example.chickenmasala.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.chickenmasala.R
import com.example.chickenmasala.databinding.ActivityMainBinding
import com.example.chickenmasala.util.Operation

class MainActivity : AppCompatActivity() {
    private val LOG_TAG = "MAIN_ACTIVITY"

    lateinit var binding: ActivityMainBinding
    val homeFragment = HomeFragment()
    val kitchenFragment = KitchenFragment()
    val triviaGamesFragment = TriviaGamesFragment()
    val indianFoodHistoryFragment = IndianFoodHistoryFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        addCallBacks()


    }

    private fun addCallBacks() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home_screen -> {
                    showFragment(homeFragment)
                    title = "Home"
                    true
                }
                R.id.kitchen_screen -> {
                    showFragment(kitchenFragment)
                    title = "kitchen"
                    true
                }
                R.id.trivia_screen -> {
                    showFragment(triviaGamesFragment)
                    title = "Trivia Screen"
                    true
                }
                R.id.indian_food_history -> {
                    showFragment(indianFoodHistoryFragment)
                    title = "Indian Food History"
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

}