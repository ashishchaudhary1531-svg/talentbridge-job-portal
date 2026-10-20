$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$tomcatBase = Join-Path $projectRoot 'target\embedded-tomcat'
$pidFile = Join-Path $tomcatBase 'talentbridge.pid'
$stopRequest = Join-Path $tomcatBase 'stop.request'

if (-not (Test-Path -LiteralPath $pidFile)) {
    Write-Host 'TalentBridge is not running.'
    exit 0
}

$pidText = (Get-Content -LiteralPath $pidFile -Raw).Trim()
[long]$processId = 0
if (-not [long]::TryParse($pidText, [ref]$processId)) {
    Write-Host 'The TalentBridge process marker is invalid. Nothing was stopped.'
    exit 1
}

try {
    $process = Get-CimInstance -ClassName Win32_Process -Filter "ProcessId = $processId"
} catch {
    Write-Host 'Could not verify the TalentBridge process. If its run.bat window is open, press Ctrl+C there.'
    exit 1
}

if (-not $process) {
    Remove-Item -LiteralPath $pidFile, $stopRequest -Force -ErrorAction SilentlyContinue
    Write-Host 'TalentBridge is already stopped.'
    exit 0
}

if ($process.CommandLine -notmatch 'com[.]talentbridge[.]StandaloneServer') {
    Write-Host 'The recorded process is not TalentBridge. Nothing was stopped.'
    exit 1
}

[System.IO.File]::WriteAllText($stopRequest, [string]$processId)
$deadline = (Get-Date).AddSeconds(20)
while ((Test-Path -LiteralPath $pidFile) -and (Get-Date) -lt $deadline) {
    Start-Sleep -Milliseconds 250
}

if (Test-Path -LiteralPath $pidFile) {
    Write-Host 'Shutdown was requested, but the server did not confirm it. Press Ctrl+C in its run.bat window.'
    exit 1
}

Write-Host 'TalentBridge has stopped.'
exit 0
