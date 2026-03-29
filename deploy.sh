#!/bin/bash
set -e

IMAGE_TAG=${1:-latest}
CONTAINER_NAME="car-rental-app"
PORT=9090

echo "Deploying car-rental-api:${IMAGE_TAG} as ${CONTAINER_NAME} on port ${PORT}"

# Record currently running image tag for potential rollback
PREVIOUS_TAG=$(docker inspect --format='{{index .Config.Image}}' ${CONTAINER_NAME} 2>/dev/null || echo "none")
echo "Previous image: ${PREVIOUS_TAG}"

# Stop and remove existing container if running
if docker ps -q -f name=${CONTAINER_NAME} | grep -q .; then
    echo "Stopping existing container..."
    docker stop ${CONTAINER_NAME}
    docker rm ${CONTAINER_NAME}
elif docker ps -aq -f name=${CONTAINER_NAME} | grep -q .; then
    echo "Removing stopped container..."
    docker rm ${CONTAINER_NAME}
fi

# Run the container
echo "Starting new container..."
docker run -d \
    --name ${CONTAINER_NAME} \
    -p ${PORT}:8080 \
    car-rental-api:${IMAGE_TAG}

echo "Deployment complete. Container ${CONTAINER_NAME} running on port ${PORT}."
