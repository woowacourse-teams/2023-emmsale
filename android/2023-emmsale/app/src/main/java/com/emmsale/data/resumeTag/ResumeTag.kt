package com.emmsale.data.resumeTag

import com.emmsale.data.resumeTag.dto.ResumeTagApiModel

data class ResumeTag(
    val name: String,
) {
    companion object {
        fun from(resumeTagApiModels: List<ResumeTagApiModel>): List<ResumeTag> =
            resumeTagApiModels.map { from(it) }

        private fun from(resumeTagApiModel: ResumeTagApiModel): ResumeTag = ResumeTag(
            name = resumeTagApiModel.name,
        )
    }
}
