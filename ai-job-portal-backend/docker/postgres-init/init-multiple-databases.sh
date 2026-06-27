#!/bin/bash
# This script runs automatically on FIRST container startup only
# (docker-entrypoint-initdb.d convention of the official postgres image).
# It creates one database per microservice inside the single shared
# PostgreSQL container/instance, per the approved "one container, multiple
# databases" decision. Each microservice still owns its database exclusively
# — no service is granted cross-database access, and no service shares a
# schema with another, preserving the "no shared database" rule even though
# they live in the same physical container.
#
# Databases are created here only for services that exist as of this
# infrastructure phase, plus the full set of business-service databases
# that will be needed in upcoming phases, so the DBA-side setup does not
# need to be revisited (and Flyway migrations per-service) every time a new
# microservice is started. No tables/schemas are created here — that is
# each service's own Flyway migration responsibility.

set -e

DATABASES=(
  "auth_service_db"
  "candidate_service_db"
  "recruiter_service_db"
  "job_service_db"
  "application_service_db"
  "ai_service_db"
  "notification_service_db"
)

for DB in "${DATABASES[@]}"; do
  echo "Creating database: $DB"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    SELECT 'CREATE DATABASE $DB'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '$DB')\gexec
EOSQL
done

echo "All microservice databases created successfully."
