# GitHub Actions Auto-Deployment Setup for Render

This document explains how to set up automatic deployment to Render using GitHub Actions.

## Overview

The GitHub Actions workflow (`/.github/workflows/deploy.yml`) will:

1. **Test Phase**: Run tests and build the application on every push/PR
2. **Deploy Phase**: Automatically deploy to Render when code is pushed to the `main` branch

## Setup Instructions

### 1. Set up Render Service

1. Go to [Render Dashboard](https://dashboard.render.com/)
2. Create a new **Web Service**
3. Connect your GitHub repository
4. Use the following settings:
   - **Environment**: Docker
   - **Dockerfile Path**: `./Dockerfile`
   - **Plan**: Free (or your preferred plan)

### 2. Get Render Deploy Hook URL

1. In your Render service dashboard, go to **Settings**
2. Scroll down to **Deploy Hook**
3. Copy the Deploy Hook URL (it looks like: `https://api.render.com/deploy/srv-xxxxxxxxxxxxx?key=xxxxxxxx`)

### 3. Configure GitHub Secrets

1. Go to your GitHub repository
2. Navigate to **Settings** > **Secrets and variables** > **Actions**
3. Click **New repository secret**
4. Add the following secret:
   - **Name**: `RENDER_DEPLOY_HOOK_URL`
   - **Value**: The deploy hook URL you copied from Render

### 4. Environment Variables in Render

Make sure your Render service has these environment variables configured:

```yaml
- key: PORT
  value: 8080
- key: SPRING_PROFILES_ACTIVE
  value: production
- key: JWT_SECRET
  generateValue: true
- key: SPRING_DATASOURCE_URL
  value: jdbc:h2:mem:testdb
- key: CORS_ALLOWED_ORIGINS
  value: https://your-frontend-domain.com,http://localhost:3000
```

### 5. Test the Deployment

1. Make a small change to your code
2. Commit and push to the `main` branch:
   ```bash
   git add .
   git commit -m "Test auto-deployment"
   git push origin main
   ```
3. Check the **Actions** tab in your GitHub repository to see the workflow running
4. Check your Render dashboard to see the deployment progress

## Workflow Details

### What happens on every push/PR:
- ✅ Code is checked out
- ✅ Java 21 is set up
- ✅ Maven dependencies are cached
- ✅ Tests are run
- ✅ Application is built
- ✅ Docker image is tested

### Additional on main branch pushes:
- 🚀 Render deployment is triggered
- 📊 Deployment status is reported

## Troubleshooting

### Common Issues:

1. **Deployment not triggered**
   - Check that `RENDER_DEPLOY_HOOK_URL` secret is correctly set
   - Verify you're pushing to the `main` branch

2. **Tests failing**
   - Check the Actions tab for detailed test output
   - Fix any failing tests before the deployment will proceed

3. **Docker build failing**
   - Ensure your `Dockerfile` is correct
   - Test Docker build locally: `docker build -t test .`

4. **Render service not starting**
   - Check Render logs in your dashboard
   - Verify environment variables are correctly configured
   - Ensure your application starts on port 8080

### Monitoring Deployments

- **GitHub Actions**: Monitor workflow execution in the Actions tab
- **Render Dashboard**: View deployment logs and service status
- **Service URL**: Test your deployed application

## Manual Deployment

If you need to manually trigger a deployment without pushing code:

1. Go to your Render service dashboard
2. Click **Manual Deploy**
3. Select the branch/commit you want to deploy

## Security Notes

- The deploy hook URL contains sensitive information - keep it in GitHub Secrets
- The workflow only deploys from the `main` branch for security
- Docker builds are tested before deployment to catch issues early
