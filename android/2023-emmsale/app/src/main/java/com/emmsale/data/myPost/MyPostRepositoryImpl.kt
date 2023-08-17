package com.emmsale.data.myPost

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.myPost.dto.MyPostApiModel
import com.emmsale.data.myPost.mapper.toData
import com.emmsale.data.token.TokenRepository

class MyPostRepositoryImpl(
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
