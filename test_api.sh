#!/bin/bash

echo "Starting application..."
java -jar target/auth-spring-api-0.0.1-SNAPSHOT.jar &
APP_PID=$!

sleep 10

echo "Testing login endpoint..."
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}' \
  -c cookies.txt -v

echo ""
echo "Checking cookies..."
cat cookies.txt

echo ""
echo "Testing refresh endpoint with refresh token from cookies..."
REFRESH_TOKEN=$(grep -o 'refresh_token[[:space:]]*[^[:space:]]*' cookies.txt | sed 's/refresh_token[[:space:]]*//')

if [ -n "$REFRESH_TOKEN" ]; then
    curl -X POST "http://localhost:8080/api/auth/refresh" \
      -H "Content-Type: application/json" \
      -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}" \
      -v
else
    echo "No refresh token found in cookies"
fi

echo ""
echo "Testing verify endpoint with cookies..."
curl -X GET "http://localhost:8080/api/auth/verify" \
  -b cookies.txt -v

kill $APP_PID
