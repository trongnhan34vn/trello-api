#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/.env"
CSV_DIR="$SCRIPT_DIR/src/main/resources/db/sample-data"
CONTAINER_CSV_DIR="/tmp/sample-data"

if [ ! -f "$ENV_FILE" ]; then
  echo "ERROR: .env file not found at $ENV_FILE"
  exit 1
fi

source "$ENV_FILE"

CONTAINER="${PG_DB_CONTAINER_NAME:-nello-db}"
DB_USER="${PG_USERNAME}"
DB_NAME="${PG_DATABASE}"

if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER}$"; then
  echo "ERROR: Container '$CONTAINER' is not running. Start it with: docker compose up -d"
  exit 1
fi

echo "Truncating existing data..."
docker exec "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" -c "
TRUNCATE TABLE
  user_sessions, card_members, board_members, workspace_members,
  checklist_items, checklists, cards, lists, boards,
  workspaces, users
RESTART IDENTITY CASCADE;
"

echo "Copying CSV files into container..."
docker cp "$CSV_DIR/." "$CONTAINER:$CONTAINER_CSV_DIR/"

FILES=(
  "04_users.csv:users"
  "05_workspaces.csv:workspaces"
  "06_boards.csv:boards"
  "07_lists.csv:lists"
  "08_cards.csv:cards"
  "09_checklists.csv:checklists"
  "10_checklist_items.csv:checklist_items"
  "11_workspace_members.csv:workspace_members"
  "12_board_members.csv:board_members"
  "13_card_members.csv:card_members"
  "14_user_sessions.csv:user_sessions"
)

for entry in "${FILES[@]}"; do
  file="${entry%%:*}"
  table="${entry##*:}"
  echo "Importing $file -> $table..."
  docker exec "$CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" \
    -c "\COPY $table FROM '$CONTAINER_CSV_DIR/$file' DELIMITER ',' CSV HEADER"
done

echo "Done. All sample data imported successfully."
