#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""分析Python报告中的custom事件错误"""

import re
import json
from pathlib import Path

def extract_custom_errors_from_report(report_path):
    """从Python报告中提取custom事件错误"""
    content = Path(report_path).read_text(encoding='utf-8')
    
    errors = []
    current_error = None
    
    for line in content.split('\n'):
        if line.startswith('### 错误 #'):
            if current_error:
                errors.append(current_error)
            current_error = {
                'error_type': None,
                'event_type': None,
                'file_path': None,
                'line_num': None,
                'error_message': None
            }
        
        if current_error:
            if line.startswith('- **事件类型**:'):
                current_error['event_type'] = line.split('`')[1]
            elif line.startswith('- **文件位置**:'):
                current_error['file_path'] = line.split('`')[1]
            elif line.startswith('- **行号**:'):
                current_error['line_num'] = int(line.split(': ')[1])
            elif line.startswith('- **错误信息**:'):
                pass  # 后面读取
    
    if current_error:
        errors.append(current_error)
    
    # 筛选出不是'message'的事件类型
    custom_errors = [e for e in errors if e['event_type'] != 'message']
    print(f"Python报告中发现 {len(custom_errors)} 个非'message'事件类型的错误")
    
    for i, err in enumerate(custom_errors[:20]):  # 显示前20个
        print(f"\n#{i+1}")
        print(f"  错误类型: {err['error_type']}")
        print(f"  事件类型: {err['event_type']}")
        print(f"  文件: {err['file_path']}")
        print(f"  行号: {err['line_num']}")
    
    return custom_errors

def count_error_types_from_report(report_path):
    """统计报告中的错误类型"""
    content = Path(report_path).read_text(encoding='utf-8')
    
    type_counts = {}
    
    # 找到统计表格部分
    table_found = False
    for line in content.split('\n'):
        if '### 错误类型分布' in line:
            table_found = True
            continue
        
        if table_found and line.startswith('|'):
            parts = [p.strip() for p in line.split('|')[1:-1]]
            if len(parts) >= 2 and parts[0] != '错误类型':
                error_type = parts[0]
                try:
                    count = int(parts[1])
                    type_counts[error_type] = count
                except:
                    pass
        elif table_found and line.startswith('---'):
            pass
        elif table_found and len(line.strip()) == 0:
            break
    
    return type_counts

def analyze_file_custom_events(file_path):
    """分析单个文件中的custom事件"""
    if not Path(file_path).exists():
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
                    if event.get('type') == 'custom' and event.get('customType'):
                        events.append({
                            'line_num': line_num,
                            'custom_type': event['customType'],
                            'data': event.get('data', {})
                        })
                except Exception:
                    pass
    except Exception as e:
        print(f"读取文件失败: {e}")
        return []
    
    print(f"\n文件 {Path(file_path).name} 中有 {len(events)} 个custom事件")
    
    for evt in events:
        print(f"  行 {evt['line_num']}: {evt['custom_type']}")
        if evt['data']:
            data_str = str(evt['data'])
            if len(data_str) > 150:
                data_str = data_str[:150] + '...'
            print(f"    data: {data_str}")
    
    return events

def main():
    print("=== 分析Python报告中的错误 ===")
    
    python_report_path = Path(r'G:\Workplace\github\clawboard\scripts\transcript-comprehensive-issues.md')
    
    type_counts = count_error_types_from_report(python_report_path)
    print("\nPython报告错误类型统计:")
    for t, c in sorted(type_counts.items()):
        print(f"  {t}: {c}")
    
    custom_errors = extract_custom_errors_from_report(python_report_path)
    
    # 查看第一个有custom事件的文件
    for err in custom_errors:
        if err['file_path']:
            print(f"\n\n=== 分析文件: {err['file_path']} ===")
            analyze_file_custom_events(err['file_path'])
            break

if __name__ == '__main__':
    main()
