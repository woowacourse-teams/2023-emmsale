package com.emmsale.data.myPost

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import okhttp3.Headers
import java.time.LocalDate

class MyPostRepositoryImpl : MyPostRepository {
    override fun getMyPosts(): ApiResult<List<MyPost>> {
        return ApiSuccess(
            List(10) {
                MyPost(
                    id = 1,
                    eventId = 11,
                    postId = 36,
                    content = "진짜 저 누구랑 가고싶은데 같이 갈 사람이 없어요... 연락주세요",
                    eventName = "인프콘 2023",
                    updatedAt = LocalDate.now(),
                )
            },
            Headers.headersOf("Auth", "good"),
        )
    }
}
