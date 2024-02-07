package com.emmsale.presentation.ui.myPostListPage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.emmsale.R
import com.emmsale.databinding.ActivityMyPostsBinding
import com.emmsale.presentation.base.BaseActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPostsActivity : BaseActivity<ActivityMyPostsBinding>(R.layout.activity_my_posts) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupFragmentStateAdapter()
        setupToolbar()
        setupMyPostsTabLayoutSelectedListener()
    }

    private fun setupFragmentStateAdapter() {
        binding.vpMyPosts.adapter =
            MyPostsFragmentStateAdapter(this)
        val tabNames = listOf(
            getString(R.string.myposts_tab_feeds),
            getString(R.string.myposts_tab_recruitments),
        )
        TabLayoutMediator(binding.tablayoutMyposts, binding.vpMyPosts) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
    }

    private fun setupMyPostsTabLayoutSelectedListener() {
        binding.tablayoutMyposts.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.vpMyPosts.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupToolbar() {
        binding.tbMyPost.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, MyPostsActivity::class.java)
            context.startActivity(intent)
        }
    }
}
