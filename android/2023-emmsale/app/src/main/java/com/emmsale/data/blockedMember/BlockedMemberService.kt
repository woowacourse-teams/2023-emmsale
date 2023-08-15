package com.emmsale.data.blockedMember

import com.emmsale.data.blockedMember.dto.BlockedMemberApiModel
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET

interface BlockedMemberService {
    @GET("/blocks")
    suspend fun getBlockedMembers(): Response<List<BlockedMemberApiModel>>

    @DELETE("/blocks/{block-id}")
    suspend fun deleteBlockedMember(blockId: Long): Response<Unit>
}