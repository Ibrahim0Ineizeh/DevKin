# Use the MinIO base image
FROM minio/minio

# Set environment variables for MinIO
ENV MINIO_ROOT_USER ibrahimineizeh
ENV MINIO_ROOT_PASSWORD atyponpassword

# Copy the bucket creation script into the container
COPY create-bucket.sh /usr/local/bin/

# Make the script executable
RUN chmod +x /usr/local/bin/create-bucket.sh

EXPOSE 9000
EXPOSE 9001

# Run the MinIO server and create the bucket
ENTRYPOINT ["/usr/local/bin/create-bucket.sh"]
CMD ["server", "/data", "--console-address", ":9001"]
