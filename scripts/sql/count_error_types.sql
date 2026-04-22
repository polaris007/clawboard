
SELECT error_type, COUNT(*) as count 
FROM dashboard_transcript_issue 
GROUP BY error_type 
ORDER BY count DESC;
