#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""对比Python和Java在同一文件上的差异"""

import json
import re
from pathlib import Path

# 复制原始脚本的ERROR_PATTERNS
ERROR_PATTERNS = {
    'modelErrors': [
        re.compile(r'model.*error', re.IGNORECASE),
        re.compile(r'api.*error', re.IGNORECASE),
        re.compile(r'LLM.*timeout', re.IGNORECASE),
        re.compile(r'operation.*aborted', re.IGNORECASE),
        re.compile(r'\baborted\b', re.IGNORECASE),
        re.compile(r'prompt.*error', re.IGNORECASE),
        re.compile(r'request.*failed', re.IGNORECASE),
        re.compile(r'inference.*error', re.IGNORECASE),
        re.compile(r'generation.*failed', re.IGNORECASE),
        re.compile(r'model.*unavailable', re.IGNORECASE),
    ],
    'timeoutErrors': [
        re.compile(r'timeout', re.IGNORECASE),
        re.compile(r'timed.*out', re.IGNORECASE),
        re.compile(r'ETIMEDOUT', re.IGNORECASE),
        re.compile(r'idle.*timeout', re.IGNORECASE),
        re.compile(r'connection.*timeout', re.IGNORECASE),
        re.compile(r'request.*timeout', re.IGNORECASE),
        re.compile(r'deadline.*exceeded', re.IGNORECASE),
    ],
    'rateLimitErrors': [
        re.compile(r'rate.*limit', re.IGNORECASE),
        re.compile(r'\b429\b', re.IGNORECASE),
        re.compile(r'too.*many.*requests', re.IGNORECASE),
        re.compile(r'quota.*exceeded', re.IGNORECASE),
        re.compile(r'throttl', re.IGNORECASE),
    ]
}


def test_custom_events_in_file(file_path):
    """测试一个文件中所有的custom事件"""
    print(f"Testing file: {file_path}")
    if not Path(file_path).exists():
        print("  File does not exist")
        return
    
    messages = []
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            for line_num, line in enumerate(f, 1):
                line = line.strip()
                if not line:
                    continue
                try:
                    event = json.loads(line)
                    messages.append({
                        'lineNum': line_num,
                        'event': event,
                        'rawLine': line
                    })
                except Exception as e:
                    pass
    except Exception as e:
        print(f"  Error reading file: {e}")
        return
    
    issues = []
    for msg in messages:
        event = msg['event']
        
        if event.get('type') == 'custom' and event.get('customType'):
            custom_type = event['customType'].lower()
            data_str = json.dumps(event.get('data') or {}, ensure_ascii=False).lower()
            
            print(f"\n  Custom event line {msg['lineNum']}, type: {event['customType']}")
            
            for category, patterns in ERROR_PATTERNS.items():
                for pattern in patterns:
                    if pattern.search(custom_type) or pattern.search(data_str):
                        error_message = event.get('data', {}).get('error') or json.dumps(event.get('data') or {}, ensure_ascii=False)
                        
                        print(f"    MATCHED {category}!")
                        print(f"      customType: {custom_type}")
                        print(f"      has data.error: {'error' in (event.get('data') or {})}")
                        print(f"      errorMessage start: {str(error_message)[:100]}")
                        
                        issues.append({
                            'lineNum': msg['lineNum'],
                            'category': category,
                            'customType': event['customType']
                        })
                        break
    
    print(f"\n  Total issues from custom events in this file: {len(issues)}")
    for issue in issues:
        print(f"    Line {issue['lineNum']} - {issue['category']} - {issue['customType']}")
    
    return issues


def test_file_with_most_issues():
    """找之前Java扫描记录的java-scanned-files.txt中的第一个文件"""
    scanned_files_path = Path(r'G:\Workplace\github\clawboard\reports\2026-04-20\java-scanned-files.txt')
    if not scanned_files_path.exists():
        print("scanned files not found")
        return
    
    files = [f.strip() for f in scanned_files_path.read_text(encoding='utf-8').split('\n') if f.strip()]
    base_path = Path(r'G:\workplace\github\clawboard\test\session-transcript')
    
    print("Scanning all files to find which have custom events...")
    
    all_custom_issues = []
    for f in files:
        full_path = base_path / f
        if not full_path.exists():
            continue
        
        issues = test_custom_events_in_file(str(full_path))
        if len(issues) > 0:
            all_custom_issues.extend([dict(i, filePath=f) for i in issues])
    
    print(f"\n\n=== SUMMARY ===")
    print(f"Total custom event issues: {len(all_custom_issues)}")
    
    counts = {}
    for issue in all_custom_issues:
        cat = issue['category']
        counts[cat] = counts.get(cat, 0) + 1
    
    for cat, cnt in sorted(counts.items()):
        print(f"  {cat}: {cnt}")
    
    print(f"\nIssues detail:")
    for issue in all_custom_issues:
        print(f"  {issue['filePath']}:{issue['lineNum']} - {issue['category']}")


def main():
    print("=== Test custom event detection in Python ===")
    test_file_with_most_issues()


if __name__ == '__main__':
    main()
