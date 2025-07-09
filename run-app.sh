#!/bin/bash

echo "Starting Internal Service on port 8081..."
java -jar internal-service/target/internal-service-1.0.0.jar &
internal_pid=$!

echo "Starting External Service on port 8082..."
java -jar external-service/target/external-service-1.0.0.jar &
external_pid=$!

echo "Starting Sync Service on port 8083..."
java -jar sync-service/target/sync-service-1.0.0.jar &
sync_pid=$!

echo "All services started."
echo "Internal Service PID: $internal_pid"
echo "External Service PID: $external_pid"
echo "Sync Service PID: $sync_pid"