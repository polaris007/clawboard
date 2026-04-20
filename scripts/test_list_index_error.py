# -*- coding: utf-8 -*-
"""
测试脚本：验证list index out of range错误的完整行记录显示功能
"""

import sys
import os
import json

# 添加scripts目录到路径
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from detect_all_transcript_issues import (
    detect_known_errors,
    detect_abnormal_stops,
    generate_markdown_report
)

def test_list_index_error_detection():
    """测试list index out of range错误的检测"""
    
    # 创建一个模拟的message事件，包含list index out of range错误
    # 注意：rawLine 是原始的JSON字符串，保持原始格式不变
    test_messages = [
        {
            'lineNum': 1,
            'event': {
                'type': 'message',
                'timestamp': '2026-04-17T02:56:46.078Z',
                'message': {
                    'role': 'assistant',
                    'errorMessage': 'list index out of range',
                    'provider': 'test-provider',
                    'model': 'test-model',
                    'stopReason': 'error'
                }
            },
            'rawLine': '{"type":"message","id":"msg_123","timestamp":"2026-04-17T02:56:46.078Z","message":{"role":"assistant","errorMessage":"list index out of range","provider":"test-provider","model":"test-model","stopReason":"error"}}'
        },
        {
            'lineNum': 2,
            'event': {
                'type': 'custom',
                'customType': 'openclaw:prompt-error',
                'timestamp': '2026-04-17T02:56:47.000Z',
                'data': {
                    'error': 'list index out of range',
                    'provider': 'test-provider',
                    'model': 'test-model'
                }
            },
            'rawLine': '{"type":"custom","customType":"openclaw:prompt-error","timestamp":"2026-04-17T02:56:47.000Z","data":{"error":"list index out of range","provider":"test-provider","model":"test-model"}}'
        },
        {
            'lineNum': 3,
            'event': {
                'type': 'message',
                'timestamp': '2026-04-17T02:56:48.000Z',
                'message': {
                    'role': 'assistant',
                    'errorMessage': 'Some other error',
                    'provider': 'test-provider',
                    'model': 'test-model',
                    'stopReason': 'error'
                }
            },
            'rawLine': '{"type":"message","id":"msg_456","timestamp":"2026-04-17T02:56:48.000Z","message":{"role":"assistant","errorMessage":"Some other error","provider":"test-provider","model":"test-model","stopReason":"error"}}'
        }
    ]
    
    print('测试1: 检测known errors中的list index out of range...')
    issues = detect_known_errors(test_messages, 'test_file.jsonl', 'test-session-id')
    
    print('找到 %d 个问题' % len(issues))
    
    for i, issue in enumerate(issues):
        print('\n问题 #%d:' % (i + 1))
        print('  错误类型: %s' % issue['errorType'])
        print('  错误信息: %s' % issue['errorMessage'])
        if 'fullLineRecord' in issue:
            print('  ✓ 包含完整行记录')
            # 验证完整行记录是原始字符串（不是重新序列化的）
            raw_record = issue['fullLineRecord']
            print('  ✓ 完整行记录类型: %s' % type(raw_record).__name__)
            print('  记录长度: %d 字符' % len(raw_record))
            # 验证可以正确解析为JSON
            try:
                record = json.loads(raw_record)
                print('  ✓ 完整行记录是有效的JSON')
                print('  记录类型: %s' % record.get('type'))
            except Exception as e:
                print('  ✗ 完整行记录解析失败: %s' % str(e))
        else:
            print('  - 不包含完整行记录')
    
    print('\n测试2: 检测abnormal stops中的list index out of range...')
    stop_issues = detect_abnormal_stops(test_messages, 'test_file.jsonl', 'test-session-id')
    
    print('找到 %d 个异常停止问题' % len(stop_issues))
    
    for i, issue in enumerate(stop_issues):
        print('\n异常停止 #%d:' % (i + 1))
        print('  停止原因: %s' % issue.get('description', 'N/A'))
        if 'fullLineRecord' in issue:
            print('  ✓ 包含完整行记录')
        else:
            print('  - 不包含完整行记录')
    
    print('\n测试3: 生成Markdown报告...')
    all_issues = issues + stop_issues
    
    if all_issues:
        markdown = generate_markdown_report(all_issues, 10, 3)
        
        # 检查报告中是否包含完整行记录
        if '完整行记录' in markdown:
            print('  ✓ Markdown报告中包含"完整行记录"字段')
            
            # 检查是否有JSON代码块
            if '```json' in markdown:
                print('  ✓ Markdown报告中包含JSON代码块')
            else:
                print('  ✗ Markdown报告中缺少JSON代码块')
        else:
            print('  ✗ Markdown报告中未找到"完整行记录"字段')
        
        # 打印一小段报告内容用于验证
        print('\n报告片段（前1000字符）:')
        print(markdown[:1000])
    else:
        print('  ⚠ 没有发现问题，无法生成报告')
    
    print('\n✅ 测试完成！')

if __name__ == '__main__':
    test_list_index_error_detection()
