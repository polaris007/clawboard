
# -*- coding: utf-8 -*-
import MySQLdb
import io

conn = MySQLdb.connect(
    host='127.0.0.1',
    port=3306,
    user='clawboard',
    passwd='Clqc@1234',
    db='clawboard',
    charset='utf8'
)

cursor = conn.cursor()

cursor.execute("SELECT error_type, event_type, session_id, line_number, file_path FROM dashboard_transcript_issue ORDER BY file_path, line_number")

rows = cursor.fetchall()

with io.open('G:\\Workplace\\github\\clawboard\\scripts\\java_issues.txt', 'w', encoding='utf-8') as f:
    for row in rows:
        error_type = row[0]
        event_type = row[1]
        session_id = row[2]
        line_number = row[3]
        file_path = row[4]
        f.write(u"{}\t{}\t{}\t{}\t{}\n".format(error_type, event_type, session_id, line_number, file_path))

print("Exported {} issues to java_issues.txt".format(len(rows)))

cursor.close()
conn.close()
