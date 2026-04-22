#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""快速对比Python和Java检测到的错误"""

import json
import os
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

def extract_errors_from_python_report():
    """从Python生成的报告中提取错误信息"""
    report_path = Path(r'G:\Workplace\github\clawboard\scripts\transcript-comprehensive-issues.md')
    if not report_path.exists():
        print("Python报告不存在")
        return
    
    content = report_path.read_text(encoding='utf-8')
    errors = []
    current_error = None
    
    for line in content.split('\n'):
        if line.startswith('### 错误 #'):
            if current_error:
                errors.append(current_error)
            current_error = {'error_type': None, 'event_type': None, 'file': None, 'line': None, 'error_msg': None}
        
        if current_error:
            if line.startswith('- **错误类型**:'):
                current_error['error_type'] = line.split('**')[3]
            elif line.startswith('- **事件类型**:'):
                current_error['event_type'] = line.split('`')[1]
            elif line.startswith('- **文件位置**:'):
                current_error['file'] = line.split('`')[1]
            elif line.startswith('- **行号**:'):
                current_error['line'] = int(line.split('**: ')[1])
            elif line.startswith('- **错误信息**:'):
                # 读取接下来的代码块
                pass
    
    # 添加最后一个错误
    if current_error:
        errors.append(current_error)
    
    print(f"Python报告中共有 {len(errors)} 个错误")
    
    # 分类统计
    error_counts = {}
    for e in errors:
        et = e['error_type']
        error_counts[et] = error_counts.get(et, 0) + 1
    
    print("\n错误类型统计:")
    for et, cnt in sorted(error_counts.items()):
        print(f"  {et}: {cnt}")
    
    return errors


def check_custom_events_in_file(file_path):
    """检查文件中的custom事件，找出可能被漏检的"""
    print(f"\n检查文件: {file_path}")
    if not os.path.exists(file_path):
        print("文件不存在")
        return
    
    custom_events = []
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            for line_num, line in enumerate(f, 1):
                line = line.strip()
                if not line:
                    continue
                
                try:
                    event = json.loads(line)
                    if event.get('type') == 'custom' and event.get('customType'):
                        custom_type = event.get('customType', '').lower()
                        data = event.get('data', {})
                        data_str = json.dumps(data, ensure_ascii=False).lower()
                        
                        # 检查是否匹配任何错误模式
                        matched = False
                        for category, patterns in ERROR_PATTERNS.items():
                            for pattern in patterns:
                                if pattern.search(custom_type) or pattern.search(data_str):
                                    error_message = data.get('error') or json.dumps(data, ensure_ascii=False)
                                    print(f"  Line {line_num}: customType={event['customType']}, category={category}")
                                    print(f"    Error: {str(error_message)[:100]}...")
                                    matched = True
                                    break
                            if matched:
                                break
                        
                        custom_events.append({
                            'line': line_num,
                            'customType': event['customType'],
                            'data': data,
                            'matched': matched
                        })
                        
                except Exception as e:
                    pass
    
    except Exception as e:
        print(f"读取文件失败: {e}")
    
    matched_count = sum(1 for e in custom_events if e['matched'])
    print(f"\n找到 {len(custom_events)} 个custom事件，其中 {matched_count} 个匹配错误模式")
    
    return custom_events


def main():
    print("=== 对比Python和Java的错误检测 ===\n")
    
    # 先看Python报告中的modelErrors和timeoutErrors
    python_errors = extract_errors_from_python_report()
    
    # 让我们也随机检查几个文件，看看custom事件
    print("\n=== 检查几个具体文件的custom事件 ===")
    
    test_files = [
        r'G:\workplace\github\clawboard\test\session-transcript\00000000016b55d6312cc47a9a702040b34c6880b1b60e13a0588c9761b49d80bd9b17344cd159e792946e08bdfc9f1ae188b56f924fb6876e694c5759ef7285\agents\main\sessions\0bef5f9c-4907-4730-bd17-cc5510fed2fc.jsonl.deleted.2026-04-13T00-53-49.392Z'
    ]
    
    # 也可以直接读取扫描文件列表，随便找几个文件
    scanned_files_path = Path(r'G:\Workplace\github\clawboard\scripts\reports\2026-04-20\python-scanned-files.txt')
    if scanned_files_path.exists():
        files = [f.strip() for f in scanned_files_path.read_text(encoding='utf-8').split('\n') if f.strip()]
        # 取前几个文件检查
        base_path = Path(r'G:\workplace\github\clawboard\test\session-transcript')
        for i, f in enumerate(files[:5]):
            full_path = base_path / f
            if full_path.exists():
                check_custom_events_in_file(str(full_path))
    else:
        print(f"扫描文件列表不存在: {scanned_files_path}")


if __name__ == '__main__':
    main()
