#!/bin/sh

set -eu

title="${1:?PR 제목이 필요합니다.}"

case "$title" in
    Draft:\ *)
        title=${title#Draft: }
        ;;
esac

if printf '%s\n' "$title" | grep -Eq '^(Feat|Fix|Refactor|Test|Docs|Chore|Build|Ci|Perf)\((api|infra|docs)\): .+'; then
    exit 0
fi

echo "PR 제목은 '<Type>(api|infra|docs): <한 줄 요약>' 형식을 따라야 합니다." >&2
echo "예시: Fix(api): 금융 상품 기간 필터 검증을 웹 경계에서 처리" >&2
exit 1
