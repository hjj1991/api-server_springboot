#!/bin/sh

set -eu

branch="${1:-$(git rev-parse --abbrev-ref HEAD)}"

case "$branch" in
    main|master|develop|release/*|hotfix/*|releases-*|dependabot/*|renovate/*)
        exit 0
        ;;
esac

if printf '%s\n' "$branch" | grep -Eq '^(codex/)?(feat|fix|refactor|test|docs|chore|build|ci|perf)/(api|infra|docs)-[a-z0-9]+(-[a-z0-9]+)*$'; then
    exit 0
fi

echo "브랜치 이름은 '(codex/)?type/(api|infra|docs)-short-summary' 형식을 따라야 합니다." >&2
echo "예시: fix/api-deposit-period-validation" >&2
exit 1
