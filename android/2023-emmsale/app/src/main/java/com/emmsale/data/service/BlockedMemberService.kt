package com.emmsale.data.service

import com.emmsale.data.apiModel.response.BlockedMemberResponse
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
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
