package com.emmsale.presentation.ui.feedDetail.recyclerView

enum class CommentsViewType(val typeNumber: Int) {
    COMMENT(0),
    CHILD_COMMENT(1),
    DELETED_COMMENT(2),
    DELETED_CHILD_COMMENT(3),
    ;

    companion object {
        fun of(typeNumber: Int): CommentsViewType = CommentsViewType.values()
            .find { it.typeNumber == typeNumber }
            ?: throw IllegalArgumentException("CommentsViewType에는 타입 번호가 ${typeNumber}인 뷰 타입이 없습니다.")
    }
}
