# Git Push Script
Set-Location "D:\MINCETUR-Project\Test_owasp\test-owasp"

Write-Host "=== Pulling from remote ===" -ForegroundColor Cyan
git pull origin main

Write-Host "`n=== Staging changes ===" -ForegroundColor Cyan
git add build.gradle.kts

Write-Host "`n=== Committing changes ===" -ForegroundColor Cyan
git commit -m "Fix: use environment variables for JFrog credentials"

Write-Host "`n=== Pushing to remote ===" -ForegroundColor Cyan
git push origin main

Write-Host "`n=== Done ===" -ForegroundColor Green

