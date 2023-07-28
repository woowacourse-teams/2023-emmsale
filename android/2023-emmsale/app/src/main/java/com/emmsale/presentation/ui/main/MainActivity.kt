package com.emmsale.presentation.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.emmsale.R
import com.emmsale.databinding.ActivityMainBinding
import com.emmsale.presentation.ui.main.events.EventsFragment
import com.emmsale.presentation.ui.main.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initBottomNavigationView()
    }

    private fun initBottomNavigationView() {
        val bnvMain = binding.bnvMain

        bnvMain.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.mi_main_profile -> changeFragment(ProfileFragment())
                R.id.mi_main_home -> changeFragment(EventsFragment())
            }
            return@setOnItemSelectedListener true
        }

        bnvMain.selectedItemId = R.id.mi_main_home
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fcv_main, fragment).commit()
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }
}