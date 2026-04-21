SELECT 
    COUNT(*) as total_turns,
    SUM(CASE WHEN system_turn = 0 THEN 1 ELSE 0 END) as non_system_turns,
    SUM(CASE WHEN system_turn = 1 THEN 1 ELSE 0 END) as system_turns,
    SUM(CASE WHEN system_turn IS NULL THEN 1 ELSE 0 END) as null_system_turns
FROM dashboard_conversation_turn;
