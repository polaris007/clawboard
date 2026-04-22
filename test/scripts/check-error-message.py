#!/usr/bin/env python3
"""Check for errorMessage field in assistant messages across transcript files."""
import json
import os
from pathlib import Path

def check_error_messages(root_dir):
    """Scan all .jsonl files and check for errorMessage in assistant messages."""
    found_with_error = []
    found_without_error = []
    
    for root, dirs, files in os.walk(root_dir):
        for file in files:
            if not file.endswith('.jsonl') or '.reset.' in file or '.deleted.' in file:
                continue
            
            filepath = os.path.join(root, file)
            try:
                with open(filepath, 'r', encoding='utf-8') as f:
                    for line_num, line in enumerate(f, 1):
                        line = line.strip()
                        if not line:
                            continue
                        try:
                            record = json.loads(line)
                            if record.get('type') == 'message':
                                msg = record.get('message', {})
                                if msg.get('role') == 'assistant':
                                    if 'errorMessage' in msg:
                                        found_with_error.append({
                                            'file': file,
                                            'line': line_num,
                                            'error_msg': msg['errorMessage'][:100] if msg['errorMessage'] else None
                                        })
                                    else:
                                        found_without_error.append({
                                            'file': file,
                                            'line': line_num,
                                            'has_stop_reason': 'stopReason' in msg,
                                            'stop_reason': msg.get('stopReason')
                                        })
                        except json.JSONDecodeError:
                            continue
            except Exception as e:
                print(f"Error reading {filepath}: {e}")
    
    return found_with_error, found_without_error

if __name__ == '__main__':
    root = r'G:\Workplace\github\clawboard\test\session-transcript\openclaw-logs'
    with_error, without_error = check_error_messages(root)
    
    print(f"\n=== Summary ===")
    print(f"Assistant messages WITH errorMessage: {len(with_error)}")
    print(f"Assistant messages WITHOUT errorMessage: {len(without_error)}")
    
    if with_error:
        print(f"\n=== Examples of messages with errorMessage ===")
        for item in with_error[:5]:
            print(f"File: {item['file']}, Line: {item['line']}")
            print(f"  Error: {item['error_msg']}")
            print()
    
    if without_error:
        print(f"\n=== Sample of messages without errorMessage ===")
        for item in without_error[:5]:
            print(f"File: {item['file']}, Line: {item['line']}")
            print(f"  Has stopReason: {item['has_stop_reason']}, Stop: {item['stop_reason']}")
