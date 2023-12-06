package com.emmsale.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.emmsale.R
import com.emmsale.databinding.ActivityMainBinding
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.eventPageList.EventFragment
import com.emmsale.presentation.ui.messageRoomList.MessageRoomFragment
import com.emmsale.presentation.ui.myProfile.MyProfileFragment
import com.emmsale.presentation.ui.setting.SettingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private var backPressedTime: Long = 0

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(binding.root)
        if (bundle == null) addAllFragments()

        initBottomNavigationView(bundle?.getInt(KEY_SELECTED_ITEM_ID))
        initBackPressedDispatcher()
    }

    private fun initBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback {
            if (binding.bnvMain.selectedItemId != R.id.mi_main_event) {
                binding.bnvMain.selectedItemId = R.id.mi_main_event
                return@addCallback
            }

            if (System.currentTimeMillis() - backPressedTime >= 2000) {
                backPressedTime = System.currentTimeMillis()
                showToast(R.string.all_finish_confirm_message)
            } else {
                finish()
            }
        }
    }

    private fun initBottomNavigationView(selectedItemId: Int? = null) {
        binding.bnvMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mi_main_event -> showFragment(EventFragment.TAG)
                R.id.mi_main_profile -> showFragment(MyProfileFragment.TAG)
                R.id.mi_main_message -> showFragment(MessageRoomFragment.TAG)
                R.id.mi_main_setting -> showFragment(SettingFragment.TAG)
            }
            return@setOnItemSelectedListener true
        }

        binding.bnvMain.selectedItemId = selectedItemId ?: R.id.mi_main_event
    }

    private fun addAllFragments() {
        supportFragmentManager.commitNow {
            add(R.id.fcv_main, MyProfileFragment(), MyProfileFragment.TAG)
            add(R.id.fcv_main, EventFragment(), EventFragment.TAG)
            add(R.id.fcv_main, MessageRoomFragment(), MessageRoomFragment.TAG)
            add(R.id.fcv_main, SettingFragment(), SettingFragment.TAG)
        }
    }

    private fun showFragment(tag: String) {
        supportFragmentManager.commit {
            supportFragmentManager.fragments.forEach(::hide)
            supportFragmentManager.findFragmentByTag(tag)?.let(::show)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_ITEM_ID, binding.bnvMain.selectedItemId)
    }

    companion object {
        private const val KEY_SELECTED_ITEM_ID = "key_selected_item_id"

        fun startActivity(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }

        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
