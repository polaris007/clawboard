
SELECT error_type, event_type, session_id, line_number, file_path 
FROM dashboard_transcript_issue 
ORDER BY file_path, line_number;
