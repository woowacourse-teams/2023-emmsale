package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.WriterResponse
import com.emmsale.data.model.Writer

fun WriterResponse.toData(): Writer = Writer(
    id = memberId,
    name = name,
    imageUrl = imageUrl,
)
