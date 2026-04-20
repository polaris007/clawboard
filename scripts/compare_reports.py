
# -*- coding: utf-8 -*-
import re
import io
from collections import defaultdict


def parse_report(file_path):
    issues = []
    with io.open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Split into sections
    sections = re.split(r'^##\s+', content, flags=re.MULTILINE)

    for section in sections:
        if not section.strip():
            continue
        lines = section.split('\n')
        first_line = lines[0].strip()

        # Check if it's an error type section
        if first_line.startswith('abnormal_stop') or first_line.startswith('modelErrors') or \
           first_line.startswith('timeoutErrors') or first_line.startswith('rateLimitErrors') or \
           first_line.startswith('flow_integrity'):
            error_type = first_line.split(' - ')[0].strip()

            # Find all issues in this section
            issue_matches = re.findall(
                r'###\s+错误\s+#\d+.*?'
                r'- \*\*文件位置\*\*:\s*`([^`]+)`.*?'
                r'- \*\*Session ID\*\*:\s*`([^`]+)`.*?'
                r'- \*\*行号\*\*:\s*(\d+)',
                section,
                flags=re.DOTALL
            )
            for match in issue_matches:
                file_path_match = match[0]
                session_id = match[1]
                line_number = int(match[2])
                issues.append({
                    'type': error_type,
                    'file': file_path_match.lower(),
                    'session': session_id,
                    'line': line_number
                })

    return issues


def compare_issues(python_issues, java_issues):
    # Create a dict for Java issues keyed by (file, session, line)
    java_issue_map = defaultdict(list)
    for issue in java_issues:
        key = (issue['file'], issue['session'], issue['line'])
        java_issue_map[key].append(issue)

    # Find issues in Python that are not in Java
    missing_from_java = []
    for issue in python_issues:
        key = (issue['file'], issue['session'], issue['line'])
        found = False
        if key in java_issue_map:
            for java_issue in java_issue_map[key]:
                if java_issue['type'] == issue['type']:
                    found = True
                    break
        if not found:
            missing_from_java.append(issue)

    return missing_from_java


if __name__ == "__main__":
    python_report = r"G:\Workplace\github\clawboard\scripts\transcript-comprehensive-issues.md"
    java_report = r"G:\Workplace\github\clawboard\reports\2026-04-20\transcript-comprehensive-issues-scan-1.md"

    print("Parsing Python report...")
    python_issues = parse_report(python_report)
    print("Found {} issues in Python report".format(len(python_issues)))

    print("\nParsing Java report...")
    java_issues = parse_report(java_report)
    print("Found {} issues in Java report".format(len(java_issues)))

    print("\nComparing issues...")
    missing = compare_issues(python_issues, java_issues)
    print("Missing from Java: {} issues".format(len(missing)))

    print("\n=== Missing issues ===")
    for idx, issue in enumerate(missing[:50]):
        print("{}. {} at {} line {} (session: {})".format(
            idx+1, issue['type'], issue['file'], issue['line'], issue['session']
        ))
    if len(missing) > 50:
        print("... and {} more".format(len(missing)-50))

    print("\n=== Missing issues by type ===")
    type_counts = defaultdict(int)
    for issue in missing:
        type_counts[issue['type']] +=1
    for t, c in sorted(type_counts.items(), key=lambda x: -x[1]):
        print("{}: {}".format(t, c))

