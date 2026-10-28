$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    & ./build.ps1
    if ($LASTEXITCODE -ne 0) { throw 'Build that bai.' }

    $tests = @(Get-ChildItem test -Filter '*Test.java' -Recurse |
        Sort-Object FullName)
    if ($tests.Count -eq 0) {
        Write-Output 'Chua co test; bo test se duoc them o buoc S8.'
        return
    }

    $testClasses = Join-Path $PSScriptRoot 'build/test-classes'
    New-Item -ItemType Directory -Force -Path $testClasses | Out-Null
    $classpath = Join-Path $PSScriptRoot 'build/classes'
    $jdbcJar = $env:HOTEL_JDBC_JAR
    if (-not $jdbcJar) {
        $jdbcJar = Join-Path $PSScriptRoot 'lib/mssql-jdbc-13.6.0.jre11.jar'
    }
    $hasJdbcDriver = Test-Path -LiteralPath $jdbcJar
    if ($hasJdbcDriver) { $classpath += ';' + $jdbcJar }

    $testSources = @($tests | ForEach-Object FullName)
    & javac -encoding UTF-8 --release 17 -cp $classpath -d $testClasses $testSources
    if ($LASTEXITCODE -ne 0) { throw 'Bien dich test that bai.' }

    $classpath += ';' + $testClasses
    foreach ($test in $tests) {
        $relative = $test.FullName.Substring((Join-Path $PSScriptRoot 'test').Length + 1)
        $relative = $relative.Substring(0, $relative.Length - '.java'.Length)
        $className = $relative -replace '[\\/]', '.'
        $javaArgs = @('-Djava.awt.headless=true')
        if (-not $hasJdbcDriver) { $javaArgs += '-Dhotel.n2.skipSqlSmoke=true' }
        $javaArgs += @('-cp', $classpath, $className)
        & java @javaArgs
        if ($LASTEXITCODE -ne 0) { throw "Test that bai: $className" }
    }
} finally {
    Pop-Location
}
