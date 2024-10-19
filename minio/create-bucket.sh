#!/bin/sh

# Start MinIO server in the background
/usr/bin/minio server /data --console-address ":9001" &

# Wait for MinIO server to become available
echo "Waiting for MinIO server to start..."
until curl -s http://localhost:9000/minio/health/live; do
    printf '.'
    sleep 5
done

echo "MinIO server is up. Proceeding to create the bucket."

# Define the bucket name
BUCKET_NAME="devkin"

# Set up MinIO client and create the bucket
mc alias set local http://localhost:9000 $MINIO_ROOT_USER $MINIO_ROOT_PASSWORD
mc mb local/$BUCKET_NAME || echo "Bucket already exists or couldn't be created."

# Keep the script running to avoid container exit
wait
