package com.emmsale.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.emmsale.R
import com.emmsale.databinding.ActivityMainBinding
import com.emmsale.presentation.ui.main.event.EventFragment
import com.emmsale.presentation.ui.main.myProfile.MyProfileFragment
import com.emmsale.presentation.ui.main.setting.SettingFragment

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (savedInstanceState == null) addAllFragments()
        initBottomNavigationView()
    }

    private fun initBottomNavigationView() {
        binding.bnvMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mi_main_profile -> showFragment(MyProfileFragment.TAG)
                R.id.mi_main_event -> showFragment(EventFragment.TAG)
                R.id.mi_main_setting -> showFragment(SettingFragment.TAG)
            }
            return@setOnItemSelectedListener true
        }

        binding.bnvMain.selectedItemId = R.id.mi_main_event
    }

    private fun addAllFragments() {
        supportFragmentManager.commitNow {
            add(R.id.fcv_main, MyProfileFragment(), MyProfileFragment.TAG)
            add(R.id.fcv_main, EventFragment(), EventFragment.TAG)
            add(
                R.id.fcv_main,
                SettingFragment(),
                SettingFragment.TAG,
            )
        }
    }

    private fun showFragment(tag: String) {
        supportFragmentManager.commit {
            val fragment = supportFragmentManager.findFragmentByTag(tag)
                ?: throw IllegalStateException("태그 ${tag}로 프래그먼트를 찾을 수 없습니다. 프래그먼트 초기화 로직을 다시 살펴보세요.")
            supportFragmentManager.fragments.forEach { hide(it) }
            show(fragment)
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }

        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
