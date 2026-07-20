#!/bin/bash

# Project Scheduler - Angular CLI Installation Script
# This script will install Angular CLI 19 globally

echo "=========================================="
echo "Angular CLI 19 Installation Script"
echo "=========================================="
echo ""

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed!"
    echo "Please install Node.js from https://nodejs.org/"
    exit 1
fi

# Check if npm is installed
if ! command -v npm &> /dev/null; then
    echo "❌ npm is not installed!"
    echo "Please install npm (comes with Node.js)"
    exit 1
fi

echo "✅ Node.js version: $(node --version)"
echo "✅ npm version: $(npm --version)"
echo ""

# Install Angular CLI
echo "Installing Angular CLI 19 globally..."
echo "This requires administrator privileges."
echo ""

sudo npm install -g @angular/cli@19

# Check if installation was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "✅ Angular CLI installed successfully!"
    echo "=========================================="
    echo ""
    echo "Verify installation:"
    ng version
    echo ""
    echo "Next steps:"
    echo "1. Create Angular project: ng new project-scheduler-frontend --routing --style=scss --standalone"
    echo "2. Navigate to project: cd project-scheduler-frontend"
    echo "3. Add Angular Material: ng add @angular/material"
    echo "4. Start development server: ng serve"
    echo ""
else
    echo ""
    echo "=========================================="
    echo "❌ Installation failed!"
    echo "=========================================="
    echo ""
    echo "Alternative installation methods:"
    echo ""
    echo "Option 1: Fix npm permissions (recommended)"
    echo "  mkdir ~/.npm-global"
    echo "  npm config set prefix '~/.npm-global'"
    echo "  echo 'export PATH=~/.npm-global/bin:\$PATH' >> ~/.bash_profile"
    echo "  source ~/.bash_profile"
    echo "  npm install -g @angular/cli@19"
    echo ""
    echo "Option 2: Use npx (no global install needed)"
    echo "  npx @angular/cli@19 new project-scheduler-frontend"
    echo ""
    echo "Option 3: Install with sudo (requires password)"
    echo "  sudo npm install -g @angular/cli@19"
    echo ""
    exit 1
fi

# Made with Bob
