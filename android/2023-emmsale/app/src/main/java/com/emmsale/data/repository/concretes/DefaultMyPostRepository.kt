package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.response.MyPostResponse
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.MyPost
import com.emmsale.data.repository.interfaces.MyPostRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.data.service.MyPostService
import javax.inject.Inject

class DefaultMyPostRepository @Inject constructor(
    private val myPostService: MyPostService,
    tokenRepository: TokenRepository,
) : MyPostRepository {
    private val myUid = requireNotNull(tokenRepository.getMyUid()) {
        "[ERROR] 로그인되지 않은 사용자입니다."
    }

    override suspend fun getMyPosts(): ApiResponse<List<MyPost>> {
        return myPostService
            .getMyPosts(myUid)
            .map(List<MyPostResponse>::toData)
    }
}
