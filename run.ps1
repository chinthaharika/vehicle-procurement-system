# Smart Vehicle Procurement System - Auto Starter
$ErrorActionPreference = 'Stop'

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  Smart Vehicle Procurement System - Auto Starter" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

# 0. Clean up existing Java processes to free port 8080
Write-Host "[Info] Checking for lingering Java processes..." -ForegroundColor Yellow
Get-Process java -ErrorAction SilentlyContinue | ForEach-Object { 
    Write-Host "Stopping process $($_.Id)..." -ForegroundColor Yellow
    Stop-Process -Id $_.Id -Force
}

# 1. Setup Java
$javaExe = "java"
try {
    $ver = & $javaExe -version 2>&1
    if ($LASTEXITCODE -ne 0) { throw "Java not in PATH" }
} catch {
    Write-Host "[INFO] Java not in PATH. Checking common locations..." -ForegroundColor Yellow
    $commonPaths = @(
        "C:\Program Files\Java\jdk-17\bin\java.exe",
        "C:\Program Files\Java\jdk-17.0.1\bin\java.exe",
        "C:\Program Files\Java\jdk-25\bin\java.exe"
    )
    
    $found = $false
    foreach ($path in $commonPaths) {
        if (Test-Path $path) {
            $javaExe = $path
            $found = $true
            Write-Host "[INFO] Found Java at: $path" -ForegroundColor Green
            break
        }
    }
    
    if (-not $found) {
        Write-Error "Java 17+ not found. Please install it manually."
        exit 1
    }
}

# 2. Setup Maven (Portable or System)
$mvnVersion = "3.9.6"
$mvnDir = Join-Path $PSScriptRoot "apache-maven-$mvnVersion"
$mvnExe = "mvn"

try {
    $ver = & $mvnExe -version 2>&1
    if ($LASTEXITCODE -ne 0) { throw "Maven not in PATH" }
} catch {
    if (Test-Path "$mvnDir\bin\mvn.cmd") {
        $mvnExe = "$mvnDir\bin\mvn.cmd"
    } else {
        Write-Host "[INFO] Maven not found. Downloading Maven $mvnVersion..." -ForegroundColor Yellow
        $url = "https://archive.apache.org/dist/maven/maven-3/$mvnVersion/binaries/apache-maven-$mvnVersion-bin.zip"
        $zipFile = "maven.zip"
        
        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
        Invoke-WebRequest -Uri $url -OutFile $zipFile
        Expand-Archive -Path $zipFile -DestinationPath $PSScriptRoot -Force
        Remove-Item $zipFile
        
        $mvnExe = "$mvnDir\bin\mvn.cmd"
    }
}

Write-Host "Using Java: $javaExe" -ForegroundColor Gray
Write-Host "Using Maven: $mvnExe" -ForegroundColor Gray

# 3. Build & Run
Write-Host "`n[Step 1/2] Installing Dependencies..." -ForegroundColor Green
& $mvnExe clean install -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Error "Build failed."
    exit 1
}

Write-Host "`n[Step 2/2] Starting Application..." -ForegroundColor Green
Write-Host "Access URL: http://localhost:8080" -ForegroundColor Cyan
Write-Host "(Press Ctrl+C to stop)" -ForegroundColor Gray
Write-Host ""

$jarFile = "target\smart-vehicle-procurement-0.0.1-SNAPSHOT.jar"
& $javaExe -jar $jarFile
