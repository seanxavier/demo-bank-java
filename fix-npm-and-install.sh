#!/bin/bash

# Project Scheduler - Fix npm permissions and install dependencies
# This script fixes npm cache permissions and installs Angular project dependencies

echo "=========================================="
echo "Fixing npm Permissions & Installing Dependencies"
echo "=========================================="
echo ""

# Fix npm cache permissions
echo "Step 1: Fixing npm cache permissions..."
echo "This requires your password."
echo ""
sudo chown -R $(whoami) "/Users/seanxaviernieva/.npm"

if [ $? -eq 0 ]; then
    echo "✅ npm cache permissions fixed!"
else
    echo "❌ Failed to fix permissions"
    exit 1
fi

echo ""
echo "Step 2: Cleaning npm cache..."
npm cache clean --force

echo ""
echo "Step 3: Installing Angular project dependencies..."
cd project-scheduler-frontend
npm install --legacy-peer-deps

if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "✅ SUCCESS! Dependencies installed!"
    echo "=========================================="
    echo ""
    echo "Next steps:"
    echo "1. cd project-scheduler-frontend"
    echo "2. ng add @angular/material --skip-confirmation"
    echo "3. ng serve"
    echo ""
    echo "Or run: ./complete-frontend-setup.sh"
    echo ""
else
    echo ""
    echo "=========================================="
    echo "❌ Installation failed"
    echo "=========================================="
    echo ""
    echo "Try manually:"
    echo "cd project-scheduler-frontend"
    echo "npm install --legacy-peer-deps --force"
    echo ""
    exit 1
fi

# Made with Bob
