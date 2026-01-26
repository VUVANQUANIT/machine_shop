# Security Notice

## ⚠️ IMPORTANT: Exposed Secrets on GitHub

Your repository has exposed sensitive information in git history:
- JWT Secret Key
- Database Credentials

## Immediate Actions Required:

### 1. Rotate All Secrets Immediately
- [ ] Generate a new JWT secret key (at least 64 characters)
- [ ] Change database password
- [ ] Update production environment variables

### 2. Add Secrets to GitHub
Go to your repository settings → Secrets and variables → Actions, and add:
- `JWT_SECRET` - Your new secure JWT secret (min 64 chars)

### 3. Clean Git History (Optional but Recommended)
```bash
# Use BFG Repo Cleaner or git filter-branch to remove secrets from history
# WARNING: This rewrites history and requires force push
git filter-branch --force --index-filter \
  'git rm --cached --ignore-unmatch src/main/resources/application.properties' \
  --prune-empty --tag-name-filter cat -- --all

# Force push (warning: this is destructive)
git push origin --force --all
```

### 4. Generate New Secure JWT Secret
```bash
# On Linux/Mac
openssl rand -base64 64

# On Windows PowerShell
[Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

## Current Security Measures Implemented:
✅ Environment variables for all secrets
✅ `.env` added to `.gitignore`
✅ `.env.example` provided for reference
✅ GitHub Actions configured to use GitHub Secrets

## Best Practices Going Forward:
1. Never commit `.env` files
2. Always use environment variables for sensitive data
3. Use GitHub Secrets for CI/CD
4. Rotate secrets regularly
5. Use strong, random secrets (64+ characters for JWT)
