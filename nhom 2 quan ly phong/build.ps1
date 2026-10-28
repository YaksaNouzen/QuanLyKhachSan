$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    $buildPath = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot 'build/classes'))
    $rootPath = [IO.Path]::GetFullPath($PSScriptRoot) + [IO.Path]::DirectorySeparatorChar
    if (-not $buildPath.StartsWith($rootPath)) { throw 'Duong dan build khong hop le.' }

    if (Test-Path -LiteralPath $buildPath) {
        Remove-Item -LiteralPath $buildPath -Recurse -Force
    }
    New-Item -ItemType Directory -Force -Path $buildPath | Out-Null
    New-Item -ItemType Directory -Force -Path (Join-Path $PSScriptRoot 'dist') | Out-Null

    $sources = @(Get-ChildItem src -Filter '*.java' -Recurse | ForEach-Object FullName)
    if ($sources.Count -eq 0) { throw 'Chua co ma Java trong src.' }
    & javac -encoding UTF-8 --release 17 -d $buildPath $sources
    if ($LASTEXITCODE -ne 0) { throw 'Bien dich Java that bai.' }

    & jar --create --file (Join-Path $PSScriptRoot 'dist/HotelPhongModule.jar') `
        --main-class app.PhongMainView -C $buildPath .
    if ($LASTEXITCODE -ne 0) { throw 'Dong goi JAR that bai.' }
} finally {
    Pop-Location
}
