# DB DIRECTORY GUIDE

## OVERVIEW
`db/` supports local PostgreSQL container runtime with mounted init/data directories.

## READ WHEN
- You touch dockerized database setup, local postgresql configuration, or seed/init scripts.

## STRUCTURE
```text
db/
|- conf.d/    # legacy db config directory (unused for postgres)
|- initdb.d/  # init scripts mounted to entrypoint (currently empty)
|- data/      # legacy runtime files from previous DB engine
|- postgres-data/ # postgres runtime data mount target
```

## WHERE TO LOOK
| Task | Location | Notes |
|------|----------|-------|
| Local DB volume wiring | `docker-compose.yaml` | binds `db/postgres-data`, `db/initdb.d` |
| DB runtime state | `db/postgres-data` | postgres container data directory |
| Startup sql/config customization | `db/initdb.d` | safe authoring location |

## CONVENTIONS
- Treat `db/postgres-data` as runtime state, not authoritative schema source.
- Put durable setup changes in `db/initdb.d`.
- Keep docker-compose and db mount paths aligned.

## ANTI-PATTERNS
- Do not edit runtime engine artifacts under `db/postgres-data`.
- Do not commit ad-hoc local DB state changes as if they were migrations.
- Do not place application code in `db/`; keep it infra-only.

## COMMANDS
```bash
docker compose up -d postgresql
docker compose down
```
