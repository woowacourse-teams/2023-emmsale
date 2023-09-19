package com.emmsale.presentation.ui.feedDetail.recyclerView

enum class CommentsViewType(val typeNumber: Int) {
    COMMENT(0),
    CHILD_COMMENT(1),
    ;

    companion object {
        fun of(typeNumber: Int): CommentsViewType =
            when (typeNumber) {
                COMMENT.typeNumber -> COMMENT
                CHILD_COMMENT.typeNumber -> CHILD_COMMENT
                else -> throw IllegalArgumentException("CommentsViewType에는 타입 번호가 ${typeNumber}인 뷰 타입이 없습니다.")
            }
    }
}
