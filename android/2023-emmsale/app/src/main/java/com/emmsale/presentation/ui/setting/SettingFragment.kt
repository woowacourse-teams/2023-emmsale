package com.emmsale.presentation.ui.setting

import com.emmsale.R
import com.emmsale.databinding.FragmentSettingBinding
import com.emmsale.presentation.base.BaseFragment

class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    override val layoutResId: Int = R.layout.fragment_setting

    companion object {
        const val TAG = "TAG_SETTING"
    }
}
