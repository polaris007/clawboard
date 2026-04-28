curl -X POST http://localhost:8080/api/v1/admin/execute-sql-file?filePath=init-complete.sql
curl -X GET http://localhost:8080/api/v1/admin/table-structure?tableName=dashboard_scan_history
curl -X GET "http://localhost:8080/api/v1/turns/12345/trace"  -H "Content-Type: application/json"
curl -X GET "http://localhost:8080/api/v1/turns/602/trace"  -H "Content-Type: application/json"
curl -X GET "http://localhost:8080/api/v1/instances/orgs/list"  -H "Content-Type: application/json"
curl -X POST "http://localhost:8080/api/v1/instances/users/search"  -H "Content-Type: application/json" --data '{"orgCodes": ["18100000", "18200000"]}'

curl -X POST http://localhost:8080/api/v1/admin/reset-database?confirmCode=20260424
curl -X POST http://localhost:8080/api/v1/scan/trigger
curl -X POST http://localhost:8080/api/v1/reports/generate-by-time-range -H "Content-Type: application/json" --data '{"startTime": "2026-04-15 00:00:00", "endTime": "2026-04-23 23:59:59"}'


