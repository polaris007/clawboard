#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""找出Python检测到但Java可能漏检的错误"""

import json
import re
import sys
from pathlib import Path


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


def analyze_file(file_path):
    """分析单个文件，找出所有custom事件和相关错误"""
    if not Path(file_path).exists():
        print(f"文件不存在: {file_path}")
        return []
    
    events = []
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            for line_num, line in enumerate(f, 1):
                line = line.strip()
                if not line:
                    continue
                try:
                    event = json.loads(line)
                    events.append({
                        'line_num': line_num,
                        'event': event
                    })
                except Exception:
                    pass
    except Exception as e:
        print(f"读取文件失败: {e}")
        return []
    
    # 分析custom事件
    custom_errors = []
    for e in events:
        event = e['event']
        if event.get('type') == 'custom' and event.get('customType'):
            custom_type = event['customType'].lower()
            data = event.get('data', {})
            data_str = json.dumps(data, ensure_ascii=False).lower()
            
            for category, patterns in ERROR_PATTERNS.items():
                for pattern in patterns:
                    if pattern.search(custom_type) or pattern.search(data_str):
                        error_message = data.get('error') or json.dumps(data, ensure_ascii=False)
                        custom_errors.append({
                            'line_num': e['line_num'],
                            'custom_type': event['customType'],
                            'category': category,
                            'error_msg': str(error_message)[:200],
                            'has_data_error': 'error' in data
                        })
                        break
    
    # 分析message事件中的assistant错误
    message_errors = []
    for e in events:
        event = e['event']
        if event.get('type') == 'message' and event.get('message') and event['message'].get('role') == 'assistant':
            error_msg = event['message'].get('errorMessage')
            if error_msg:
                error_msg_lower = error_msg.lower()
                for category, patterns in ERROR_PATTERNS.items():
                    for pattern in patterns:
                        if pattern.search(error_msg_lower):
                            message_errors.append({
                                'line_num': e['line_num'],
                                'category': category,
                                'error_msg': error_msg[:200]
                            })
                            break
    
    return custom_errors, message_errors


def main():
    print("=== 查找可能的差异 ===\n")
    
    # 读取扫描的文件列表
    scanned_files = []
    files_list_path = Path(r'G:\Workplace\github\clawboard\scripts\reports\2026-04-20\python-scanned-files.txt')
    if files_list_path.exists():
        scanned_files = [f.strip() for f in files_list_path.read_text(encoding='utf-8').split('\n') if f.strip()]
    
    base_path = Path(r'G:\workplace\github\clawboard\test\session-transcript')
    
    total_custom_errors = 0
    total_message_errors = 0
    all_custom_errors = []
    all_message_errors = []
    
    # 分析每个文件
    print("分析文件中的错误...\n")
    for f in scanned_files:
        full_path = base_path / f
        if full_path.exists():
            custom_errors, message_errors = analyze_file(str(full_path))
            
            if custom_errors:
                print(f"文件: {f}")
                for err in custom_errors:
                    print(f"  [Custom] Line {err['line_num']}: {err['category']} (has_data_error={err['has_data_error']})")
                    print(f"    {err['custom_type']}: {err['error_msg']}")
                    all_custom_errors.append({**err, 'file': f})
                total_custom_errors += len(custom_errors)
            
            if message_errors:
                for err in message_errors:
                    all_message_errors.append({**err, 'file': f})
                total_message_errors += len(message_errors)
    
    print(f"\n总计:")
    print(f"  Custom事件错误: {total_custom_errors}")
    print(f"  Message事件错误: {total_message_errors}")
    
    # 分类统计custom错误
    print(f"\nCustom错误分类:")
    cat_counts = {}
    for err in all_custom_errors:
        cat = err['category']
        cat_counts[cat] = cat_counts.get(cat, 0) + 1
    for cat, cnt in sorted(cat_counts.items()):
        print(f"  {cat}: {cnt}")
    
    # 特别关注那些有data.error的
    print(f"\n有data.error的custom错误:")
    for err in all_custom_errors:
        if err['has_data_error']:
            print(f"  {err['file']}:{err['line_num']} - {err['category']}")
            print(f"    {err['error_msg']}")


if __name__ == '__main__':
    main()
