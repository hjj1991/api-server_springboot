#!/usr/bin/env bash

set -euo pipefail

message_file="${1:?commit message file is required}"
allowed_types='Feat|Fix|Refactor|Test|Docs|Chore|Build|Ci|Perf'
allowed_scopes='api|infra|docs'

content="$(awk '!/^[[:space:]]*#/ { print }' "$message_file")"
subject="$(printf '%s\n' "$content" | awk 'NF { print; exit }')"

if [[ -z "$subject" ]]; then
  echo "커밋 메시지가 비어 있습니다." >&2
  exit 1
fi

if [[ "$subject" =~ ^(Merge|Revert)\  ]]; then
  exit 0
fi

if ! printf '%s\n' "$subject" | grep -Eq "^(${allowed_types})\\((${allowed_scopes})\\): .+"; then
  echo "커밋 제목은 '<Type>(api|infra|docs): <한 줄 요약>' 형식을 따라야 합니다." >&2
  echo "예시: Fix(api): 금융 상품 기간 필터 검증을 웹 경계에서 처리" >&2
  exit 1
fi

type="${subject%%(*}"

has_bullet_under_heading() {
  local heading="$1"
  printf '%s\n' "$content" | awk -v heading="$heading" '
    $0 == heading { in_section=1; next }
    in_section && /^[[:space:]]*$/ { next }
    in_section && /^[[:space:]]*-/ { found=1; exit 0 }
    in_section && !/^[[:space:]]*-/ { exit 1 }
    END { exit found ? 0 : 1 }
  '
}

case "$type" in
  Feat|Fix|Refactor|Test)
    if ! printf '%s\n' "$content" | grep -Eq '^변경 내용$'; then
      echo "${type} 커밋은 본문에 '변경 내용' 섹션이 필요합니다." >&2
      exit 1
    fi
    if ! printf '%s\n' "$content" | grep -Eq '^검증$'; then
      echo "${type} 커밋은 본문에 '검증' 섹션이 필요합니다." >&2
      exit 1
    fi
    if ! has_bullet_under_heading "변경 내용"; then
      echo "'변경 내용' 아래에는 최소 한 개의 '-' 항목이 필요합니다." >&2
      exit 1
    fi
    if ! has_bullet_under_heading "검증"; then
      echo "'검증' 아래에는 최소 한 개의 '-' 항목이 필요합니다." >&2
      exit 1
    fi
    ;;
esac
