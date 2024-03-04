package com.emmsale.data.network.service

import com.emmsale.data.network.apiModel.response.BlockedMemberResponse
import com.emmsale.data.network.callAdapter.ApiResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface BlockedMemberService {
    @GET("/blocks")
    suspend fun getBlockedMembers(): ApiResponse<List<BlockedMemberResponse>>

    @DELETE("/blocks/{block-id}")
    suspend fun deleteBlockedMember(
        @Path("block-id") blockId: Long,
    ): ApiResponse<Unit>
}
