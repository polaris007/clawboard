
# -*- coding: utf-8 -*-
import re
import io

python_report = r"G:\Workplace\github\clawboard\scripts\transcript-comprehensive-issues.md"
with io.open(python_report, 'r', encoding='utf-8') as f:
    content = f.read()

print("Content length:", len(content))
print("\nFirst 200 chars:")
print(repr(content[:200]))

print("\nLooking for 'abnormal_stop':")
print('"abnormal_stop" in content:', 'abnormal_stop' in content)
print('"modelErrors" in content:', 'modelErrors' in content)

print("\nLooking for '文件位置':")
if '文件位置' in content:
    idx = content.index('文件位置')
    print("Found at index", idx)
    print("Context around:", repr(content[idx-50:idx+150]))
