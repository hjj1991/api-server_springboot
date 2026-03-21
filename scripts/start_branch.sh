#!/usr/bin/env bash

set -euo pipefail

scope="api"
use_codex_prefix=1
print_only=0
ticket=""

usage() {
  cat <<'EOF'
Usage: ./scripts/start_branch.sh <type> <slug> [--ticket <id>] [--no-codex] [--print-only]

Examples:
  ./scripts/start_branch.sh fix deposit-period-validation --ticket 123 --print-only
  ./scripts/start_branch.sh feat financial-history-page
EOF
}

if [[ $# -lt 2 ]]; then
  usage >&2
  exit 1
fi

type="$1"
slug="$2"
shift 2

while [[ $# -gt 0 ]]; do
  case "$1" in
    --ticket)
      ticket="${2:-}"
      if [[ -z "$ticket" ]]; then
        echo "--ticket requires a value." >&2
        exit 1
      fi
      shift 2
      ;;
    --no-codex)
      use_codex_prefix=0
      shift
      ;;
    --print-only)
      print_only=1
      shift
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
done

if ! [[ "$type" =~ ^(feat|fix|refactor|test|docs|chore|build|ci|perf)$ ]]; then
  echo "type must be one of: feat, fix, refactor, test, docs, chore, build, ci, perf" >&2
  exit 1
fi

if ! [[ "$slug" =~ ^[a-z0-9]+(-[a-z0-9]+)*$ ]]; then
  echo "slug must be lowercase kebab-case." >&2
  exit 1
fi

if [[ -n "$ticket" ]] && ! [[ "$ticket" =~ ^[a-zA-Z0-9]+(-[a-zA-Z0-9]+)*$ ]]; then
  echo "ticket must be a simple identifier like 123 or sprint-12." >&2
  exit 1
fi

work_key="$slug"
if [[ -n "$ticket" ]]; then
  work_key="${ticket}-${slug}"
fi

branch="${type}/${scope}-${work_key}"
if [[ "$use_codex_prefix" -eq 1 ]]; then
  branch="codex/${branch}"
fi

if [[ "$print_only" -eq 1 ]]; then
  printf '%s\n' "$branch"
  exit 0
fi

current_branch="$(git rev-parse --abbrev-ref HEAD)"
if [[ "$current_branch" == "$branch" ]]; then
  echo "$branch"
  exit 0
fi

if git show-ref --verify --quiet "refs/heads/${branch}"; then
  git switch "$branch"
else
  git switch -c "$branch"
fi

printf '%s\n' "$branch"
