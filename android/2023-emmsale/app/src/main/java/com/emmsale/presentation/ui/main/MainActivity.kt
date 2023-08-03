package com.emmsale.presentation.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.emmsale.R
import com.emmsale.databinding.ActivityMainBinding
import com.emmsale.presentation.ui.comment.CommentsFragment
import com.emmsale.presentation.ui.main.events.EventsFragment
import com.emmsale.presentation.ui.main.myProfile.MyProfileFragment

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
        val mainBottomNavigationView = binding.bnvMain

        addAllFragments()

        mainBottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mi_main_profile -> showFragment(MyProfileFragment.TAG)
                R.id.mi_main_home -> showFragment(EventsFragment.TAG)
                R.id.mi_main_setting -> showFragment(CommentsFragment.TAG)
            }
            return@setOnItemSelectedListener true
        }

        mainBottomNavigationView.selectedItemId = R.id.mi_main_home
    }

    private fun addAllFragments() {
        supportFragmentManager.commitNow {
            add(R.id.fcv_main, MyProfileFragment(), MyProfileFragment.TAG)
            add(R.id.fcv_main, EventsFragment(), EventsFragment.TAG)
            add(
                R.id.fcv_main,
                CommentsFragment().apply { arguments = Bundle().apply { putLong("eventId", 1L) } },
                CommentsFragment.TAG
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
    }
}
