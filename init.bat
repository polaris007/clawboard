curl -X POST http://localhost:8080/api/v1/admin/execute-sql-file?filePath=init-complete.sql
curl -X GET http://localhost:8080/api/v1/admin/table-structure?tableName=dashboard_scan_history