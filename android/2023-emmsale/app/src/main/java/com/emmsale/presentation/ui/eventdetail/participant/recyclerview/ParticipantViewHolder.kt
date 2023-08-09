package com.emmsale.presentation.ui.eventdetail.participant.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemParticipantBinding
import com.emmsale.presentation.eventdetail.participant.uistate.ParticipantUiState

class ParticipantViewHolder(
    private val binding: ItemParticipantBinding,
    private val requestCompanion: (Long, String) -> Unit,
    private val showMemberProfile: (Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.buttonRequestCompanion.setOnClickListener {
            requestCompanion(
                binding.participant!!.memberId,
                binding.participant!!.name,
            )
        }
        binding.ivParticipantImage.setOnClickListener { showMemberProfile(binding.participant!!.memberId) }
    }

    fun bind(participant: ParticipantUiState) {
        binding.participant = participant
    }

    companion object {
        fun create(
            parent: ViewGroup,
            requestCompanion: (Long, String) -> Unit,
            showMemberProfile: (Long) -> Unit,
        ): ParticipantViewHolder {
            val binding = ItemParticipantBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )

            return ParticipantViewHolder(binding, requestCompanion, showMemberProfile)
        }
    }
}
