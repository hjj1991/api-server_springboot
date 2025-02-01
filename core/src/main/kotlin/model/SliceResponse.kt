package model

data class SliceResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean,
    val first: Boolean,
    val last: Boolean
) {
    companion object {
        fun <T> of(
            content: List<T>,
            page: Int,
            size: Int,
            hasNext: Boolean,
            first: Boolean,
            last: Boolean
        ): SliceResponse<T> {
            return SliceResponse(
                content = content,
                page = page,
                size = size,
                hasNext = hasNext,
                first = first,
                last = last
            )
        }
    }
}
