package com.emmsale.presentation.ui.myPostList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityMyPostBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPostActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMyPostBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initFragmentStateAdapter()
    }

    private fun initFragmentStateAdapter() {
        binding.vpMyPost.adapter = MyPostListFragmentStateAdapter(this)
        val tabNames =
            listOf(
                getString(R.string.eventdetail_tab_post),
                getString(R.string.eventdetail_tab_recruitment),
            )
        TabLayoutMediator(binding.tlMyPost, binding.vpMyPost) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, MyPostActivity::class.java)
            context.startActivity(intent)
        }
    }
}
