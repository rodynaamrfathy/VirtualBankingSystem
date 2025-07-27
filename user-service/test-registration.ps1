$body = @{
    username = "test.user"
    password = "securePassword123"
    email = "test.user@example.com"
    firstName = "Test"
    lastName = "User"
} | ConvertTo-Json

Write-Host "Testing registration endpoint..."
Write-Host "Request body: $body"

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/users/register" -Method POST -Body $body -ContentType "application/json"
    Write-Host "Response received:"
    Write-Host ($response | ConvertTo-Json -Depth 10)
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response body: $responseBody"
    }
} 