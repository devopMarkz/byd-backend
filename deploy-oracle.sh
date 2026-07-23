#!/bin/bash

# Deployment Script for Oracle Cloud Always Free Tier
# Ride Profit Backend

set -e

echo "=== Ride Profit Backend - Oracle Cloud Deployment ==="

# Configuration
IMAGE_NAME="rideprofit-backend"
CONTAINER_NAME="rideprofit-backend"
POSTGRES_CONTAINER="rideprofit-postgres"
NETWORK_NAME="rideprofit-network"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if .env file exists
if [ ! -f .env ]; then
    echo -e "${RED}Error: .env file not found!${NC}"
    echo "Please copy .env.example to .env and configure your environment variables."
    exit 1
fi

# Load environment variables
export $(cat .env | grep -v '^#' | xargs)

echo -e "${YELLOW}Step 1: Building Docker image...${NC}"
docker build -t $IMAGE_NAME:latest .

echo -e "${YELLOW}Step 2: Creating Docker network...${NC}"
docker network create $NETWORK_NAME 2>/dev/null || echo "Network already exists"

echo -e "${YELLOW}Step 3: Stopping existing containers...${NC}"
docker stop $CONTAINER_NAME 2>/dev/null || true
docker stop $POSTGRES_CONTAINER 2>/dev/null || true

echo -e "${YELLOW}Step 4: Removing existing containers...${NC}"
docker rm $CONTAINER_NAME 2>/dev/null || true
docker rm $POSTGRES_CONTAINER 2>/dev/null || true

echo -e "${YELLOW}Step 5: Starting PostgreSQL container...${NC}"
docker run -d \
  --name $POSTGRES_CONTAINER \
  --network $NETWORK_NAME \
  -e POSTGRES_DB=${DB_NAME:-rideprofit} \
  -e POSTGRES_USER=${DB_USER:-rideprofit} \
  -e POSTGRES_PASSWORD=${DB_PASSWORD:-rideprofit} \
  -v rideprofit-postgres-data:/var/lib/postgresql/data \
  --cpus="1.0" \
  --memory="512m" \
  postgres:16-alpine

echo -e "${YELLOW}Waiting for PostgreSQL to be ready...${NC}"
sleep 10

echo -e "${YELLOW}Step 6: Starting Backend container...${NC}"
docker run -d \
  --name $CONTAINER_NAME \
  --network $NETWORK_NAME \
  -e DB_HOST=$POSTGRES_CONTAINER \
  -e DB_PORT=${DB_PORT:-5432} \
  -e DB_NAME=${DB_NAME:-rideprofit} \
  -e DB_USER=${DB_USER:-rideprofit} \
  -e DB_PASSWORD=${DB_PASSWORD:-rideprofit} \
  -e JWT_SECRET=${JWT_SECRET} \
  -e SERVER_PORT=${SERVER_PORT:-8080} \
  -e SPRING_PROFILES_ACTIVE=prod \
  -p ${SERVER_PORT:-8080}:8080 \
  --cpus="1.0" \
  --memory="1g" \
  $IMAGE_NAME:latest

echo -e "${YELLOW}Step 7: Waiting for application to start...${NC}"
sleep 30

echo -e "${YELLOW}Step 8: Checking health status...${NC}"
HEALTH_CHECK_URL="http://localhost:${SERVER_PORT:-8080}/api/actuator/health"
MAX_RETRIES=10
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
  if curl -f -s $HEALTH_CHECK_URL > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Application is healthy!${NC}"
    curl -s $HEALTH_CHECK_URL
    break
  else
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo "Attempt $RETRY_COUNT/$MAX_RETRIES - Application not ready yet..."
    sleep 5
  fi
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
  echo -e "${RED}✗ Health check failed after $MAX_RETRIES attempts${NC}"
  echo "Check logs with: docker logs $CONTAINER_NAME"
  exit 1
fi

echo -e "${GREEN}=== Deployment completed successfully! ===${NC}"
echo -e "API: ${GREEN}http://localhost:${SERVER_PORT:-8080}/api${NC}"
echo -e "Health Check: ${GREEN}http://localhost:${SERVER_PORT:-8080}/api/actuator/health${NC}"
echo -e "Swagger UI: ${GREEN}http://localhost:${SERVER_PORT:-8080}/api/swagger-uihtml${NC}"
echo ""
echo "Useful commands:"
echo "  View logs: docker logs -f $CONTAINER_NAME"
echo "  Stop: docker stop $CONTAINER_NAME $POSTGRES_CONTAINER"
echo "  Restart: docker restart $CONTAINER_NAME"
