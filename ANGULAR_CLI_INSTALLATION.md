# Angular CLI Installation Guide

## 🚨 Permission Issue Detected

The automatic installation failed due to npm permissions. This is a common issue on macOS/Linux systems.

## ✅ Your System Info
- **Node.js**: v24.13.0 ✅
- **npm**: 11.6.2 ✅
- **Operating System**: macOS Sequoia

---

## 📋 Installation Options

### **Option 1: Run the Installation Script (Recommended)**

I've created a script that will handle the installation for you:

```bash
./install-angular-cli.sh
```

**This will prompt you for your password** to install Angular CLI globally with sudo.

---

### **Option 2: Manual Installation with sudo**

Open your terminal and run:

```bash
sudo npm install -g @angular/cli@19
```

Enter your password when prompted.

---

### **Option 3: Fix npm Permissions (Best Long-term Solution)**

This prevents needing sudo for future global npm installations:

```bash
# Create a directory for global packages
mkdir ~/.npm-global

# Configure npm to use the new directory
npm config set prefix '~/.npm-global'

# Add the new directory to your PATH
echo 'export PATH=~/.npm-global/bin:$PATH' >> ~/.bash_profile

# Reload your profile
source ~/.bash_profile

# Now install Angular CLI without sudo
npm install -g @angular/cli@19
```

---

### **Option 4: Use npx (No Global Install)**

If you don't want to install Angular CLI globally, use npx:

```bash
# Create new Angular project directly
npx @angular/cli@19 new project-scheduler-frontend --routing --style=scss --standalone

# Use ng commands with npx
cd project-scheduler-frontend
npx ng serve
npx ng generate component my-component
```

**Note**: This downloads Angular CLI each time, which is slower but doesn't require global installation.

---

## 🔍 Verify Installation

After installation, verify Angular CLI is installed:

```bash
ng version
```

You should see output like:

```
Angular CLI: 19.x.x
Node: 24.13.0
Package Manager: npm 11.6.2
OS: darwin arm64
```

---

## 📦 Next Steps After Installation

Once Angular CLI is installed, create the frontend project:

### 1. Create Angular Project

```bash
ng new project-scheduler-frontend --routing --style=scss --standalone
```

**Options to select:**
- Would you like to add Angular routing? **Yes**
- Which stylesheet format would you like to use? **SCSS**
- Do you want to enable Server-Side Rendering (SSR)? **No**

### 2. Navigate to Project

```bash
cd project-scheduler-frontend
```

### 3. Add Angular Material

```bash
ng add @angular/material
```

**Options to select:**
- Choose a prebuilt theme: **Indigo/Pink** (or your preference)
- Set up global Angular Material typography styles? **Yes**
- Include the Angular animations module? **Yes**

### 4. Start Development Server

```bash
ng serve
```

Open browser to: http://localhost:4200

---

## 🎯 Quick Start Commands

After Angular CLI is installed:

```bash
# Create project
ng new project-scheduler-frontend --routing --style=scss --standalone

# Navigate to project
cd project-scheduler-frontend

# Add Angular Material
ng add @angular/material

# Start dev server
ng serve

# Open in browser
open http://localhost:4200
```

---

## 🐛 Troubleshooting

### Issue: "ng: command not found"

**Solution**: Add npm global bin to PATH:

```bash
echo 'export PATH="$(npm config get prefix)/bin:$PATH"' >> ~/.bash_profile
source ~/.bash_profile
```

### Issue: "EACCES: permission denied"

**Solution**: Use Option 3 above to fix npm permissions permanently.

### Issue: Angular CLI version mismatch

**Solution**: Uninstall and reinstall:

```bash
sudo npm uninstall -g @angular/cli
sudo npm install -g @angular/cli@19
```

---

## 📞 Need Help?

If you encounter any issues:

1. Check Node.js version: `node --version` (should be v18+)
2. Check npm version: `npm --version`
3. Clear npm cache: `npm cache clean --force`
4. Try Option 4 (npx) as a fallback

---

## ✨ What's Next?

After successful installation and project creation, we'll build:

1. **TypeScript Models** - Project, Task, Dependency, Schedule interfaces
2. **Angular Services** - HTTP communication with Spring Boot backend
3. **Components**:
   - Project form (create/edit projects)
   - Task form (with Fibonacci story points selector)
   - Dependency manager (visual dependency selection)
   - Schedule viewer (table with Gantt-like display)
4. **Routing** - Navigation between views
5. **Material Design** - Professional UI with Angular Material
6. **Validation** - Form validation and error handling
7. **Testing** - Unit and E2E tests

---

## 🎉 Ready to Continue?

Once you've successfully installed Angular CLI and created the project, let me know and we'll start building the frontend components!

**Current Status**: Waiting for Angular CLI installation ⏳