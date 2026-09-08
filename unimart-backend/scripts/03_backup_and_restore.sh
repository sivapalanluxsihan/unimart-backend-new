#!/usr/bin/env bash
# 03_backup_and_restore.sh
# Local mysqldump backup / restore helpers. See Guide 04, section 7.
#
# Usage:
#   ./scripts/03_backup_and_restore.sh backup
#   ./scripts/03_backup_and_restore.sh restore path/to/unimart_backup.sql
#
# Requires the MySQL client tools (mysqldump, mysql) to be on PATH and the
# unimart_app account created by 01_create_schema_and_user.sql.

set -euo pipefail

ACTION="${1:-}"
DB_NAME="unimart"
DB_USER="unimart_app"

case "$ACTION" in
  backup)
    OUT_FILE="unimart_backup_$(date +%Y%m%d_%H%M%S).sql"
    mysqldump -u "$DB_USER" -p --single-transaction \
      --routines --triggers "$DB_NAME" > "$OUT_FILE"
    echo "Backup written to $OUT_FILE"
    ;;
  restore)
    FILE="${2:-}"
    if [[ -z "$FILE" ]]; then
      echo "Usage: $0 restore path/to/unimart_backup.sql" >&2
      exit 1
    fi
    echo "This will load data into the '$DB_NAME' schema. Use an empty"
    echo "development schema, not production, unless you truly intend this."
    read -r -p "Continue? [y/N] " confirm
    if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
      echo "Aborted."
      exit 1
    fi
    mysql -u "$DB_USER" -p "$DB_NAME" < "$FILE"
    echo "Restore complete."
    ;;
  *)
    echo "Usage: $0 {backup|restore <file>}" >&2
    exit 1
    ;;
esac
