package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.response.MyPostApiModel
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.MyPost
import com.emmsale.data.repository.interfaces.MyPostRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.data.service.MyPostService

class DefaultMyPostRepository(
    private val myPostService: MyPostService,
    tokenRepository: TokenRepository,
) : MyPostRepository {
    private val myUid = tokenRepository.getMyUid() ?: throw IllegalStateException("로그인 되지 않았어요!!!!")
    override suspend fun getMyPosts(): ApiResult<List<MyPost>> {
        return handleApi(
            execute = { myPostService.getMyPosts(myUid) },
            mapToDomain = List<MyPostApiModel>::toData,
        )
    }
}
