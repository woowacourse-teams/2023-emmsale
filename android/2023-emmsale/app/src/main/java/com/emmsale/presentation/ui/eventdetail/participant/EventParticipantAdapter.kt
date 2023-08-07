package com.emmsale.presentation.ui.eventdetail.participant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemParticipantBinding
import com.emmsale.presentation.eventdetail.participant.uistate.ParticipantUiState

class EventParticipantAdapter(
    private val requestCompanion: (Long, String) -> Unit,
    private val showMemberProfile: (Long) -> Unit,
) : ListAdapter<ParticipantUiState, ParticipantViewHolder>(diffUtil) {

    private lateinit var binding: ItemParticipantBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        binding = ItemParticipantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ParticipantViewHolder(binding, requestCompanion, showMemberProfile)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ParticipantUiState>() {
            override fun areItemsTheSame(
                oldItem: ParticipantUiState,
                newItem: ParticipantUiState,
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: ParticipantUiState,
                newItem: ParticipantUiState,
            ): Boolean = oldItem.id == newItem.id
        }
    }
}

class ParticipantViewHolder(
    private val binding: ItemParticipantBinding,
    private val requestCompanion: (Long, String) -> Unit,
    private val showMemberProfile: (Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
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
