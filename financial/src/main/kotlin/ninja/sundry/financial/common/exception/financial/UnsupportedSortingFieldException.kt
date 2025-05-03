package ninja.sundry.financial.common.exception.financial

class UnsupportedSortingFieldException(field: String) : RuntimeException("지원되지 않는 정렬 필드입니다: $field")
