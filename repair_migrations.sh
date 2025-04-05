#!/bin/bash
echo "Repairing Flyway migrations..."
mvn flyway:repair
echo "Done."
