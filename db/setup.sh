#!/bin/bash
rm -f ../chat_project.sqlite
sqlite3 ../chat_project.sqlite < db-setup.sql
echo "Successfully created database: chat_project.sqlite is ready!"
