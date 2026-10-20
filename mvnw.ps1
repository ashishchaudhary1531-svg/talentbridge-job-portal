param([Parameter(ValueFromRemainingArguments = $true)][string[]]$MavenArguments)
$ErrorActionPreference = 'Stop'

$installed = Get-Command mvn.cmd -ErrorAction SilentlyContinue
if ($installed) {
    & $installed.Source @MavenArguments
    exit $LASTEXITCODE
}

$version = '3.10.0'
$root = Join-Path $env:USERPROFILE ".m2\wrapper\dists\apache-maven-$version"
$mavenHomePath = Join-Path $root "apache-maven-$version"
$maven = Join-Path $mavenHomePath 'bin\mvn.cmd'
if (-not (Test-Path -LiteralPath $maven)) {
    New-Item -ItemType Directory -Path $root -Force | Out-Null
    $archive = Join-Path $root "apache-maven-$version-bin.zip"
    $checksum = "$archive.sha512"
    $baseUrl = "https://dlcdn.apache.org/maven/maven-3/$version/binaries/apache-maven-$version-bin.zip"
    Write-Host "Downloading Apache Maven $version from the Apache mirror..."
    Invoke-WebRequest -Uri $baseUrl -OutFile $archive
    Invoke-WebRequest -Uri "$baseUrl.sha512" -OutFile $checksum
    $expected = (Get-Content -LiteralPath $checksum -Raw).Trim().Split([char[]]@(' ', "`t", "`r", "`n"), [System.StringSplitOptions]::RemoveEmptyEntries)[0]
    $hasher = [System.Security.Cryptography.SHA512]::Create()
    $stream = [System.IO.File]::OpenRead($archive)
    try { $actual = [System.BitConverter]::ToString($hasher.ComputeHash($stream)).Replace('-', '') }
    finally { $stream.Dispose(); $hasher.Dispose() }
    if ($actual -ne $expected) {
        Remove-Item -LiteralPath $archive, $checksum -Force
        throw 'The Maven archive checksum did not match the Apache SHA-512 file.'
    }
    Expand-Archive -LiteralPath $archive -DestinationPath $root -Force
    Remove-Item -LiteralPath $archive, $checksum -Force
}

& $maven @MavenArguments
exit $LASTEXITCODE
