#!/usr/bin/env sh
set -e

echo "Waiting for MinIO..."
until mc alias set local http://minio:9000 "$MINIO_ROOT_USER" "$MINIO_ROOT_PASSWORD" >/dev/null 2>&1; do
  sleep 2
done

echo "Creating bucket if not exists: $MINIO_BUCKET"
mc mb -p "local/$MINIO_BUCKET" >/dev/null 2>&1 || true

echo "Bucket ready."