# -*- coding: utf-8 -*-
"""
OpenClaw Transcript 综合问题检测器 (Python 2.7 版本)

合并所有检测逻辑：
1. 对话流程完整性检测（user→assistant, toolCall→toolResult, toolResult→assistant）
2. 已知错误模式检测（modelErrors, timeoutErrors等）
3. 异常停止检测（stopReason异常）

使用方法：
  python detect-all-transcript-issues.py [transcript_dir] [--start-time TIME] [--end-time TIME]

参数说明：
  transcript_dir: 可选，指定要扫描的transcript目录路径
                 如果不提供，默认使用 logs/session-transcript/openclaw-logs
  --start-time, --start: 可选，起始时间，只扫描此时间之后创建的文件
                        支持格式: YYYY-MM-DD, YYYY-MM-DDTHH:MM:SS, YYYY-MM-DD HH:MM:SS
                        例如: 2026-04-01 或 2026-04-01T10:30:00
  --end-time, --end: 可选，结束时间，只扫描此时间之前创建的文件
                     支持格式: YYYY-MM-DD, YYYY-MM-DDTHH:MM:SS, YYYY-MM-DD HH:MM:SS
                     例如: 2026-04-15 或 2026-04-15T18:00:00

示例：
  # 扫描所有文件
  python detect-all-transcript-issues.py
  
  # 扫描指定目录
  python detect-all-transcript-issues.py logs/session-transcript/openclaw-logs
  
  # 只扫描2026-04-01之后的文件
  python detect-all-transcript-issues.py --start 2026-04-01
  
  # 只扫描2026-04-01到2026-04-15之间的文件
  python detect-all-transcript-issues.py --start 2026-04-01 --end 2026-04-15
  
  # 扫描指定目录和时间范围
  python detect-all-transcript-issues.py logs/session-transcript/openclaw-logs --start 2026-04-10 --end 2026-04-15
"""

from __future__ import unicode_literals, print_function
import os
import sys
import re
import json
import random
import csv
import hashlib
from datetime import datetime

# Python 2.7 兼容性处理
if sys.version_info[0] == 2:
    # 设置默认编码为 UTF-8
    reload(sys)
    sys.setdefaultencoding('utf-8')

    # 设置 PYTHONIOENCODING 环境变量，确保所有 I/O 使用 UTF-8
    os.environ['PYTHONIOENCODING'] = 'utf-8'

    # Windows 控制台编码修复
    if sys.platform == 'win32':
        try:
            import codecs
            sys.stdout = codecs.getwriter('utf-8')(sys.stdout)
            sys.stderr = codecs.getwriter('utf-8')(sys.stderr)
        except:
            pass
    else:
        # Linux/Mac 环境：确保 stdout/stderr 使用 UTF-8
        import codecs
        if hasattr(sys.stdout, 'encoding') and sys.stdout.encoding is None:
            sys.stdout = codecs.getwriter('utf-8')(sys.stdout)
        if hasattr(sys.stderr, 'encoding') and sys.stderr.encoding is None:
            sys.stderr = codecs.getwriter('utf-8')(sys.stderr)


def safe_print(text):
    """
    安全打印 Unicode 字符串，兼容 Python 2.7 的所有平台

    Args:
        text: 要打印的文本（可以是 unicode 或 str）
    """
    if sys.version_info[0] == 2:
        if isinstance(text, unicode):
            # Unicode 字符串，编码为 UTF-8
            try:
                print(text.encode('utf-8'))
            except UnicodeEncodeError:
                # 如果编码失败，尝试忽略错误字符
                print(text.encode('utf-8', 'ignore'))
        else:
            # 已经是字节字符串，直接打印
            print(text)
    else:
        # Python 3 直接打印
        print(text)

    # 强制刷新缓冲区，确保 nohup 模式下能立即看到输出
    sys.stdout.flush()

# ==================== 错误模式定义 ====================


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
    ],
    'toolErrors': [
        re.compile(r'tool.*error', re.IGNORECASE),
        re.compile(r'tool.*failed', re.IGNORECASE),
        re.compile(r'execution.*error', re.IGNORECASE),
        re.compile(r'command.*failed', re.IGNORECASE),
        re.compile(r'tool.*timeout', re.IGNORECASE),
    ],
    'permissionErrors': [
        re.compile(r'permission.*denied', re.IGNORECASE),
        re.compile(r'access.*denied', re.IGNORECASE),
        re.compile(r'forbidden', re.IGNORECASE),
        re.compile(r'\b403\b', re.IGNORECASE),
        re.compile(r'unauthorized', re.IGNORECASE),
        re.compile(r'auth.*error', re.IGNORECASE),
    ],
    'parsingErrors': [
        re.compile(r'parse.*error', re.IGNORECASE),
        re.compile(r'invalid.*json', re.IGNORECASE),
        re.compile(r'syntax.*error', re.IGNORECASE),
        re.compile(r'malformed', re.IGNORECASE),
        re.compile(r'unexpected.*token', re.IGNORECASE),
    ],
    'networkErrors': [
        re.compile(r'network.*error', re.IGNORECASE),
        re.compile(r'connection.*refused', re.IGNORECASE),
        re.compile(r'ECONNREFUSED', re.IGNORECASE),
        re.compile(r'ENOTFOUND', re.IGNORECASE),
        re.compile(r'socket.*hang.*up', re.IGNORECASE),
        re.compile(r'fetch.*failed', re.IGNORECASE),
        re.compile(r'dns.*error', re.IGNORECASE),
    ],
}

ERROR_TYPE_NAMES = {
    'modelErrors': '模型API错误',
    'timeoutErrors': '超时错误',
    'rateLimitErrors': '速率限制错误',
    'toolErrors': '工具执行错误',
    'permissionErrors': '权限错误',
    'parsingErrors': '解析错误',
    'networkErrors': '网络错误',
}

# ==================== 账户映射功能 ====================


def load_accounts_mapping(script_dir):
    """
    从 accounts.csv 加载工号到人员信息的映射

    Returns:
        dict: {sha512_hash: {'name': str, 'employee_id': str, 'department': str}}
    """
    csv_path = os.path.join(script_dir, 'accounts.csv')
    if not os.path.exists(csv_path):
        return None

    print('📋 检测到 accounts.csv，正在加载账户映射...')
    mapping = {}

    try:
        with open(csv_path, 'rb') as f:
            reader = csv.reader(f)
            header = next(reader)  # 跳过表头

            for row in reader:
                if len(row) < 4:
                    continue

                # CSV格式：序号,姓名,工号,部门,机构号
                employee_id = row[2].strip()  # 第三列是工号

                # 解码 GBK 编码的中文
                try:
                    name = row[1].decode('gbk').strip()  # 第二列是姓名
                    department = row[3].decode('gbk').strip()  # 第四列是部门
                except:
                    # 如果解码失败，使用原始值
                    name = row[1].strip()
                    department = row[3].strip()

                if not employee_id:
                    continue

                # 计算工号的 SHA512 哈希
                hash_value = hashlib.sha512(
                    employee_id.encode('utf-8')).hexdigest()

                mapping[hash_value] = {
                    'name': name,
                    'employee_id': employee_id,
                    'department': department,
                }

        print('✅ 成功加载 %d 个账户映射\n' % len(mapping))
        return mapping
    except Exception as e:
        print('⚠️ 加载 accounts.csv 失败: %s\n' % str(e))
        return None


def extract_employee_from_path(file_path, accounts_mapping):
    """
    从文件路径中提取员工信息

    Args:
        file_path: transcript 文件路径
        accounts_mapping: 账户映射字典

    Returns:
        dict or None: {'name': str, 'employee_id': str, 'department': str} 或 None
    """
    if not accounts_mapping:
        return None

    # 规范化路径
    normalized_path = os.path.normpath(file_path)

    # 查找 "agents" 目录的前一级目录名（即 SHA512 哈希值）
    parts = normalized_path.replace('\\', '/').split('/')

    # 找到 "agents" 的索引
    try:
        agents_index = parts.index('agents')
        if agents_index > 0:
            hash_value = parts[agents_index - 1]

            # 在映射中查找
            if hash_value in accounts_mapping:
                return accounts_mapping[hash_value]
    except ValueError:
        pass

    return None


# ==================== 辅助函数 ====================


def generate_id():
    """生成随机ID"""
    return ''.join(random.choice('abcdefghijklmnopqrstuvwxyz0123456789') for _ in range(13))


def format_timestamp(ts):
    """格式化时间戳"""
    if not ts:
        return 'N/A'
    if isinstance(ts, (int, float)):
        return datetime.utcfromtimestamp(ts).isoformat() + 'Z'
    return ts


def analyze_cause(category, error_message, context):
    """分析错误原因"""
    msg = error_message.lower()

    if category == 'modelErrors':
        if 'timeout' in msg or 'idle' in msg:
            return '模型服务响应超时，可能原因：1) 网络延迟或不稳定；2) 模型服务端负载过高；3) Prompt过长导致处理时间增加；4) 前置工具执行失败导致模型等待用户输入'
        if 'aborted' in msg or 'cancel' in msg:
            return '请求被中止，可能原因：1) 用户主动取消操作；2) 系统资源限制触发中止；3) 会话超时被清理；4) 新请求到来时旧请求被取消'
        if 'context' in msg or 'token' in msg:
            return '上下文长度超限，可能原因：1) 会话历史过长；2) 单次输入内容过多；3) 未正确配置max_tokens参数；4) 缺少Compaction机制导致上下文累积'
        return '模型API调用失败，可能原因：1) API密钥无效或过期；2) 模型服务暂时不可用；3) 请求格式不正确；4) 配额已用完'

    elif category == 'timeoutErrors':
        if 'idle' in msg:
            return '空闲超时，可能原因：1) 用户长时间未输入；2) 工具执行时间过长；3) 网络中断导致连接保持但无数据传输'
        return '请求超时，可能原因：1) 网络延迟过高；2) 服务端处理缓慢；3) 防火墙或代理拦截；4) DNS解析超时'

    elif category == 'rateLimitErrors':
        return '触发速率限制，可能原因：1) 短时间内请求过于频繁；2) 超过API配额限制；3) 多个实例共享同一API密钥；4) 未实现请求排队或退避机制'

    elif category == 'toolErrors':
        if 'permission' in msg or 'denied' in msg:
            return '工具执行权限不足，可能原因：1) 文件系统权限限制；2) 沙箱环境约束；3) 需要sudo权限但未配置；4) 访问受限资源'
        return '工具执行失败，可能原因：1) 命令不存在或路径错误；2) 依赖未安装；3) 输入参数不正确；4) 工具内部逻辑错误'

    elif category == 'permissionErrors':
        return '权限验证失败，可能原因：1) API密钥无效；2) OAuth token过期；3) IP白名单限制；4) 账户被禁用或欠费'

    elif category == 'parsingErrors':
        return '数据解析失败，可能原因：1) JSON格式不正确；2) 编码问题；3) 数据结构与预期不符；4) 特殊字符未转义'

    elif category == 'networkErrors':
        if 'refused' in msg:
            return '连接被拒绝，可能原因：1) 目标服务未启动；2) 端口未开放；3) 防火墙阻止；4) 服务地址配置错误'
        if 'notfound' in msg or 'dns' in msg:
            return 'DNS解析失败，可能原因：1) 域名拼写错误；2) DNS服务器故障；3) 网络连接中断；4) hosts文件配置问题'
        return '网络连接错误，可能原因：1) 网络不稳定；2) 代理配置错误；3) SSL证书问题；4) 服务端重启或维护'

    else:
        return '未知错误类型，需要人工分析具体原因'


def extract_context_info(line_num, messages):
    """提取上下文信息"""
    try:
        current_index = None
        for i, m in enumerate(messages):
            if m['lineNum'] == line_num:
                current_index = i
                break

        if current_index is None:
            return ''

        current_msg = messages[current_index]
        next_msg = messages[current_index +
                            1] if current_index + 1 < len(messages) else None

        context = '\n--- 错误行内容 ---\n'
        context += 'Line %d: %s\n' % (line_num, json.dumps(
            current_msg['event'], ensure_ascii=False)[:500])

        if next_msg:
            context += '\n--- 下一行内容 ---\n'
            context += 'Line %d: %s\n' % (next_msg['lineNum'], json.dumps(
                next_msg['event'], ensure_ascii=False)[:500])

        return context
    except Exception as e:
        return '\n[提取上下文失败]'


def extract_user_input(current_line_num, current_role, messages):
    """提取用户输入（从当前或最近的user消息中）"""
    try:
        current_index = None
        for i, m in enumerate(messages):
            if m['lineNum'] == current_line_num:
                current_index = i
                break

        if current_index is None:
            return '[无法定位当前消息]'

        # 如果当前是user消息，直接从当前消息提取
        if current_role == 'user':
            current_msg = messages[current_index]
            user_content = extract_text_from_message(
                current_msg['event'].get('message'))
            return user_content if user_content else '[user消息内容为空]'

        # 如果不是user消息，向前查找最近的user消息
        for i in range(current_index - 1, -1, -1):
            msg = messages[i]
            event_msg = msg['event'].get('message')
            if event_msg and event_msg.get('role') == 'user':
                user_content = extract_text_from_message(event_msg)
                return user_content if user_content else '[user消息内容为空]'

        return '[未找到user消息]'
    except Exception as e:
        return '[提取用户输入失败]'


def extract_text_from_message(message):
    """从message对象中提取文本内容"""
    if not message or not message.get('content'):
        return ''

    content = message['content']

    # content可能是字符串或数组
    if isinstance(content, basestring):
        return content

    # content是数组时，提取所有text类型的内容
    if isinstance(content, list):
        texts = []
        for item in content:
            if isinstance(item, dict) and item.get('type') == 'text' and item.get('text'):
                texts.append(item['text'])
        return '\n'.join(texts)

    return ''


def is_system_generated_user_message(content):
    """判断user消息是否为系统生成（而非真实用户输入）"""
    system_patterns = [
        re.compile(r'A new session was started via /new or /reset',
                   re.IGNORECASE),
        re.compile(r'Run your Session Startup sequence', re.IGNORECASE),
        re.compile(r'Read HEARTBEAT\.md if it exists', re.IGNORECASE),
        re.compile(r'<<<BEGIN_OPENCLAW_INTERNAL_CONTEXT>>>', re.IGNORECASE),
        re.compile(r'^System:\s*\[', re.IGNORECASE),  # 系统状态消息
    ]

    for pattern in system_patterns:
        if pattern.search(content):
            return True
    return False


def has_tool_call(event):
    """检查是否有工具调用"""
    message = event.get('message')
    if not message or not message.get('content'):
        return False
    content = message['content']
    if isinstance(content, list):
        for block in content:
            if isinstance(block, dict) and block.get('type') == 'toolCall':
                return True
    return False

# ==================== 核心检测函数 ====================


def detect_flow_integrity(messages, file_path, session_id):
    """检测1: 对话流程完整性"""
    issues = []

    for i in range(len(messages)):
        current = messages[i]
        next_msg = messages[i + 1] if i + 1 < len(messages) else None

        # 规则1: user后面必须要有assistant
        event_msg = current['event'].get('message')
        if event_msg and event_msg.get('role') == 'user':
            # 跳过系统生成的user消息（如会话启动提示）
            user_content = extract_text_from_message(event_msg)
            if is_system_generated_user_message(user_content):
                continue  # 忽略系统消息，不进行检测

            if not next_msg:
                context_info = extract_context_info(
                    current['lineNum'], messages)
                user_input = extract_user_input(
                    current['lineNum'], event_msg.get('role'), messages)
                issues.append({
                    'id': generate_id(),
                    'errorType': 'flow_integrity_no_reply',
                    'eventType': 'message',
                    'description': '用户提问后没有任何回复（文件在此结束）',
                    'errorMessage': 'Expected assistant message after user message, but reached end of file\n%s' % context_info,
                    'causeAnalysis': '可能的原因：1) 会话被意外中断；2) 系统崩溃导致回复丢失；3) 网络断开；4) 用户主动终止会话但未记录',
                    'filePath': file_path,
                    'sessionId': session_id,
                    'lineNumber': current['lineNum'],
                    'timestamp': current['event'].get('timestamp'),
                    'userInput': user_input,
                    'severity': 'HIGH',
                })
            elif next_msg['event'].get('message') and next_msg['event']['message'].get('role') != 'assistant':
                # 特殊规则：如果下一条是openclaw:prompt-error等错误事件，说明请求被中止，跳过避免重复统计
                if next_msg['event'].get('type') == 'custom' and next_msg['event'].get('customType', '').find('prompt-error') >= 0:
                    continue  # 这个错误会在detect_known_errors中被统计为modelErrors

                context_info = extract_context_info(
                    current['lineNum'], messages)
                user_input = extract_user_input(
                    current['lineNum'], event_msg.get('role'), messages)
                issues.append({
                    'id': generate_id(),
                    'errorType': 'flow_integrity_no_reply',
                    'eventType': 'message',
                    'description': '用户提问后的下一条消息角色是"%s"，而非预期的assistant' % next_msg['event']['message'].get('role'),
                    'errorMessage': 'Expected "assistant" after "user", but got "%s"\n%s' % (next_msg['event']['message'].get('role'), context_info),
                    'causeAnalysis': '可能的原因：1) 系统状态异常导致跳过回复；2) 消息顺序错乱；3) Compaction/Reset操作导致的记录不完整；4) 并发请求导致消息交错',
                    'filePath': file_path,
                    'sessionId': session_id,
                    'lineNumber': current['lineNum'],
                    'timestamp': current['event'].get('timestamp'),
                    'userInput': user_input,
                    'severity': 'HIGH',
                })

        # 规则2: toolCall后面必须要有toolResult
        if event_msg and event_msg.get('role') == 'assistant' and has_tool_call(current['event']):
            # 特殊规则1：如果assistant消息被aborted或error，说明toolCall未执行，跳过检测
            stop_reason = event_msg.get('stopReason')
            if stop_reason == 'aborted' or stop_reason == 'error':
                continue  # 跳过，这是正常的中止场景

            # 特殊规则2：如果下一条是openclaw:prompt-error等错误事件，说明toolCall被中止，跳过避免重复统计
            if next_msg and next_msg['event'].get('type') == 'custom' and next_msg['event'].get('customType', '').find('prompt-error') >= 0:
                continue  # 这个错误会在detect_known_errors中被统计为modelErrors

            if not next_msg:
                context_info = extract_context_info(
                    current['lineNum'], messages)
                user_input = extract_user_input(
                    current['lineNum'], event_msg.get('role'), messages)
                issues.append({
                    'id': generate_id(),
                    'errorType': 'flow_integrity_missing_tool_result',
                    'eventType': 'message',
                    'description': 'Assistant调用了工具但没有收到工具执行结果（文件在此结束）',
                    'errorMessage': 'Expected toolResult after toolCall, but reached end of file\n%s' % context_info,
                    'causeAnalysis': '可能的原因：1) 工具执行超时或被中断；2) 工具执行过程中系统崩溃；3) 日志记录不完整；4) 工具异步执行但未等待结果',
                    'filePath': file_path,
                    'sessionId': session_id,
                    'lineNumber': current['lineNum'],
                    'timestamp': current['event'].get('timestamp'),
                    'userInput': user_input,
                    'severity': 'HIGH',
                })
            elif next_msg['event'].get('message') and next_msg['event']['message'].get('role') != 'toolResult':
                context_info = extract_context_info(
                    current['lineNum'], messages)
                user_input = extract_user_input(
                    current['lineNum'], event_msg.get('role'), messages)
                issues.append({
                    'id': generate_id(),
                    'errorType': 'flow_integrity_missing_tool_result',
                    'eventType': 'message',
                    'description': 'Assistant调用工具后的下一条消息角色是"%s"，而非预期的toolResult' % next_msg['event']['message'].get('role'),
                    'errorMessage': 'Expected "toolResult" after "toolCall", but got "%s"\n%s' % (next_msg['event']['message'].get('role'), context_info),
                    'causeAnalysis': '可能的原因：1) 工具执行失败但未记录错误；2) 消息顺序错乱；3) 工具被跳过直接继续对话；4) 日志损坏或缺失',
                    'filePath': file_path,
                    'sessionId': session_id,
                    'lineNumber': current['lineNum'],
                    'timestamp': current['event'].get('timestamp'),
                    'userInput': user_input,
                    'severity': 'HIGH',
                })

        # 规则3: toolResult后面必须要有assistant
        if event_msg and event_msg.get('role') == 'toolResult':
            tool_name = event_msg.get('toolName')

            if tool_name == 'sessions_yield':
                continue

            if not next_msg:
                # toolResult是最后一条消息，没有后续
                context_info = extract_context_info(
                    current['lineNum'], messages)
                user_input = extract_user_input(
                    current['lineNum'], event_msg.get('role'), messages)
                issues.append({
                    'id': generate_id(),
                    'errorType': 'flow_integrity_missing_final_answer',
                    'eventType': 'message',
                    'description': '工具执行完成后没有Assistant的最终回复（文件在此结束）',
                    'errorMessage': 'Expected assistant message after toolResult, but reached end of file\n%s' % context_info,
                    'causeAnalysis': '可能的原因：1) Assistant在处理工具结果时出错；2) 会话被意外终止；3) 工具结果过于复杂导致无法生成回复；4) 系统资源耗尽',
                    'filePath': file_path,
                    'sessionId': session_id,
                    'lineNumber': current['lineNum'],
                    'timestamp': current['event'].get('timestamp'),
                    'userInput': user_input,
                    'severity': 'MEDIUM',
                })
            elif next_msg['event'].get('message') and next_msg['event']['message'].get('role') != 'assistant' and next_msg['event']['message'].get('role') != 'toolResult':
                # 特殊规则：如果下一条是openclaw:prompt-error等错误事件，说明已经被detect_known_errors捕获，跳过避免重复统计
                if next_msg['event'].get('type') == 'custom' and next_msg['event'].get('customType', '').find('prompt-error') >= 0:
                    # 跳过，这个错误会在detect_known_errors中被统计为modelErrors或timeoutErrors
                    continue

                # toolResult后面既不是assistant也不是另一个toolResult，说明流程异常
                context_info = extract_context_info(
                    current['lineNum'], messages)
                user_input = extract_user_input(
                    current['lineNum'], event_msg.get('role'), messages)
                issues.append({
                    'id': generate_id(),
                    'errorType': 'flow_integrity_missing_final_answer',
                    'eventType': 'message',
                    'description': '工具执行完成后的下一条消息角色是"%s"，而非预期的assistant最终回复或另一个toolResult' % next_msg['event']['message'].get('role'),
                    'errorMessage': 'Expected "assistant" or "toolResult" after "toolResult", but got "%s"\n%s' % (next_msg['event']['message'].get('role'), context_info),
                    'causeAnalysis': '可能的原因：1) Assistant未能正确处理工具结果；2) 触发了新的用户输入打断流程；3) 消息顺序异常；4) 并发请求导致消息交错',
                    'filePath': file_path,
                    'sessionId': session_id,
                    'lineNumber': current['lineNum'],
                    'timestamp': current['event'].get('timestamp'),
                    'userInput': user_input,
                    'severity': 'MEDIUM',
                })
            # 注意：如果next是toolResult，这是正常的并行工具调用场景，不报错

    return issues


def detect_known_errors(messages, file_path, session_id):
    """检测2: 已知错误模式"""
    issues = []

    for msg in messages:
        event = msg['event']

        # 检测custom事件中的错误
        if event.get('type') == 'custom' and event.get('customType'):
            custom_type = event['customType'].lower()
            data_str = json.dumps(event.get('data') or {},
                                  ensure_ascii=False).lower()

            for category, patterns in ERROR_PATTERNS.items():
                for pattern in patterns:
                    if pattern.search(custom_type) or pattern.search(data_str):
                        error_message = event.get('data', {}).get('error') or json.dumps(
                            event.get('data') or {}, ensure_ascii=False)
                        # 提取用户输入
                        user_input = extract_user_input(
                            msg['lineNum'], 'assistant', messages)
                        
                        issue = {
                            'id': generate_id(),
                            'errorType': category,
                            'eventType': event['customType'],
                            'description': '检测到%s事件' % ERROR_TYPE_NAMES[category],
                            'errorMessage': error_message[:500],
                            'causeAnalysis': analyze_cause(category, error_message, event.get('data')),
                            'filePath': file_path,
                            'sessionId': session_id,
                            'lineNumber': msg['lineNum'],
                            'timestamp': format_timestamp(event.get('timestamp') or (event.get('data') or {}).get('timestamp')),
                            'runId': (event.get('data') or {}).get('runId'),
                            'provider': (event.get('data') or {}).get('provider'),
                            'model': (event.get('data') or {}).get('model'),
                            'userInput': user_input,
                            'severity': 'HIGH',
                        }
                        
                        # 如果错误信息包含"list index out of range"，添加原始的完整行记录
                        if 'list index out of range' in error_message.lower():
                            issue['fullLineRecord'] = msg.get('rawLine', '')
                        
                        issues.append(issue)
                        break

        # 检测message中的错误（仅针对assistant角色）
        if event.get('type') == 'message' and event.get('message') and event['message'].get('role') == 'assistant':
            error_message = event['message'].get('errorMessage')

            if not error_message:
                continue

            error_msg_lower = error_message.lower()

            for category, patterns in ERROR_PATTERNS.items():
                for pattern in patterns:
                    if pattern.search(error_msg_lower):
                        # 提取用户输入
                        user_input = extract_user_input(
                            msg['lineNum'], 'assistant', messages)
                        
                        issue = {
                            'id': generate_id(),
                            'errorType': category,
                            'eventType': 'message',
                            'description': '在message事件中检测到%s' % ERROR_TYPE_NAMES[category],
                            'errorMessage': error_message[:500],
                            'causeAnalysis': analyze_cause(category, error_message, event['message']),
                            'filePath': file_path,
                            'sessionId': session_id,
                            'lineNumber': msg['lineNum'],
                            'timestamp': format_timestamp(event.get('timestamp')),
                            'provider': event['message'].get('provider'),
                            'model': event['message'].get('model'),
                            'userInput': user_input,
                            'severity': 'HIGH',
                        }
                        
                        # 如果错误信息包含"list index out of range"，添加原始的完整行记录
                        if 'list index out of range' in error_message.lower():
                            issue['fullLineRecord'] = msg.get('rawLine', '')
                        
                        issues.append(issue)
                        break

    return issues


def detect_abnormal_stops(messages, file_path, session_id):
    """检测3: 异常停止"""
    issues = []
    normal_stop_reasons = ['stop', 'toolUse', 'length']

    for msg in messages:
        event = msg['event']
        if event.get('type') == 'message' and event.get('message') and event['message'].get('stopReason'):
            stop_reason = event['message']['stopReason']

            if stop_reason not in normal_stop_reasons:
                error_message = event['message'].get(
                    'errorMessage') or 'Unexpected stop reason: %s' % stop_reason
                # 提取用户输入
                user_input = extract_user_input(
                    msg['lineNum'], 'assistant', messages)

                issue = {
                    'id': generate_id(),
                    'errorType': 'abnormal_stop',
                    'eventType': 'message',
                    'description': '检测到异常停止原因: %s' % stop_reason,
                    'errorMessage': error_message[:500],
                    'causeAnalysis': analyze_cause('modelErrors', error_message, event['message']),
                    'filePath': file_path,
                    'sessionId': session_id,
                    'lineNumber': msg['lineNum'],
                    'timestamp': format_timestamp(event.get('timestamp')),
                    'provider': event['message'].get('provider'),
                    'model': event['message'].get('model'),
                    'userInput': user_input,
                    'severity': 'HIGH' if stop_reason in ['aborted', 'error'] else 'MEDIUM',
                }
                
                # 如果错误信息包含"list index out of range"，添加原始的完整行记录
                if error_message and 'list index out of range' in error_message.lower():
                    issue['fullLineRecord'] = msg.get('rawLine', '')
                
                issues.append(issue)

    return issues


def analyze_transcript(file_path, accounts_mapping=None):
    """分析单个transcript文件"""
    all_issues = []
    conversation_turns = 0  # 真实对话轮数
    problematic_turns = 0   # 有错误的对话轮数

    # 提取员工信息
    employee_info = extract_employee_from_path(file_path, accounts_mapping)

    try:
        # Windows 长路径支持：添加 \\?\ 前缀
        if sys.platform == 'win32' and len(file_path) > 260:
            # 确保路径是绝对路径
            file_path = os.path.abspath(file_path)
            # 添加 UNC 前缀以支持长路径
            long_path = u'\\\\?\\' + file_path
        else:
            long_path = file_path

        with open(long_path, 'r') as f:
            content = f.read()

        lines = [line for line in content.split('\n') if line.strip()]

        # 提取session ID
        session_id = 'unknown'
        for line in lines:
            try:
                parsed = json.loads(line)
                if parsed.get('type') == 'session' and parsed.get('id'):
                    session_id = parsed['id']
                    break
            except:
                # 跳过无效行
                pass

        # 解析所有message事件
        messages = []

        for i, line in enumerate(lines):
            try:
                event = json.loads(line)

                if event.get('type') in ['message', 'custom']:
                    messages.append({
                        'lineNum': i + 1,
                        'event': event,
                        'rawLine': line,  # 保存原始行内容
                    })

                    # 统计真实用户消息（排除系统生成）
                    if event.get('type') == 'message' and event.get('message') and event['message'].get('role') == 'user':
                        user_content = extract_text_from_message(
                            event['message'])
                        if not is_system_generated_user_message(user_content):
                            conversation_turns += 1
            except:
                # 跳过无效行
                pass

        # 执行三种检测
        flow_issues = detect_flow_integrity(messages, file_path, session_id)
        known_error_issues = detect_known_errors(
            messages, file_path, session_id)
        abnormal_stop_issues = detect_abnormal_stops(
            messages, file_path, session_id)

        all_issues.extend(flow_issues)
        all_issues.extend(known_error_issues)
        all_issues.extend(abnormal_stop_issues)

        # 将员工信息添加到每个问题中
        if employee_info:
            for issue in all_issues:
                issue['employee'] = employee_info

        # 统计有错误的对话轮数（存在任何类型错误的轮次）
        problematic_turn_set = set()
        for issue in all_issues:
            # 所有错误的lineNumber都对应触发该错误的消息行号
            problematic_turn_set.add(issue['lineNumber'])
        problematic_turns = len(problematic_turn_set)

    except Exception as e:
        print('Error analyzing %s: %s' % (file_path, str(e)))

    return {
        'issues': all_issues,
        'conversationTurns': conversation_turns,
        'problematicTurns': problematic_turns,
        'employee': employee_info,
    }


def find_jsonl_files(dir_path):
    """查找所有JSONL文件（匹配包含.jsonl的文件名）"""
    import platform

    # Linux/Mac 优先使用 find 命令（速度快 5-10 倍）
#    if platform.system() != 'Windows':
#        try:
#            return _find_jsonl_files_external(dir_path)
#        except Exception as e:
#            safe_print('   ⚠️ find 命令失败，降级到 os.walk: %s' % str(e))

    # Windows 或降级使用 os.walk
    return _find_jsonl_files_walk(dir_path)


def _find_jsonl_files_external(dir_path):
    """使用系统 find 命令查找文件（Linux/Mac，最快）"""
    import subprocess

    dir_path = os.path.normpath(dir_path)
    safe_print('🔍 正在使用 find 命令扫描目录树...')

    # 构建 find 命令
    # -type f: 只查找文件
    # -name '*.jsonl*': 匹配包含 .jsonl 的文件名
    # ! -name '*.swp': 排除 .swp 文件
    cmd = [
        'find', dir_path, '-type', 'f',
        '(', '-name', '*.jsonl*', ')',
        '!', '-name', '*.swp'
    ]

    try:
        result = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            shell=False
        )
        stdout, stderr = result.communicate()

        if result.returncode == 0:
            files = [f.strip()
                     for f in stdout.strip().split('\n') if f.strip()]
            safe_print('   ✅ find 命令扫描完成，找到 %d 个 JSONL 文件\n' % len(files))
            return files
        else:
            raise Exception(stderr.strip())
    except Exception as e:
        raise e


def _find_jsonl_files_walk(dir_path):
    """使用 os.walk 查找文件（Windows 或降级方案）"""
    results = []
    # 规范化路径，解析 .. 等符号
    dir_path = os.path.normpath(dir_path)

    safe_print('[INFO] Scanning directory tree...')
    dir_count = 0
    file_count = 0
    last_progress_time = 0

    import time
    start_time = time.time()

    # 使用 os.walk() 而非递归 os.listdir()，避免深层目录扫描问题
    for root, dirs, files in os.walk(dir_path):
        dir_count += 1
        current_time = time.time()

        # 每 0.5 秒或每 100 个目录显示一次进度
        if dir_count % 100 == 0 or (current_time - last_progress_time) >= 0.5:
            elapsed = current_time - start_time
            safe_print('   [PROGRESS] Scanned %d directories, found %d files... (%.1fs)' % (
                dir_count, len(results), elapsed))
            last_progress_time = current_time

        for filename in files:
            file_count += 1
            # 只处理 .jsonl 相关文件，排除 .swp 等临时文件
            if '.jsonl' in filename and not filename.endswith('.swp'):
                # 处理所有包含 .jsonl 的文件，包括 .jsonl.reset、.jsonl.deleted 等归档文件
                full_path = os.path.join(root, filename)
                results.append(full_path)

    elapsed_time = time.time() - start_time
    safe_print('   [INFO] Scan complete: traversed %d directories, %d files, found %d JSONL files (%.1fs)\n' % (
        dir_count, file_count, len(results), elapsed_time))

    # Save scanned files list for comparison
    report_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'reports', datetime.now().strftime('%Y-%m-%d'))
    if not os.path.exists(report_dir):
        os.makedirs(report_dir)
    files_list_path = os.path.join(report_dir, 'python-scanned-files.txt')
    with open(files_list_path, 'w') as f:
        for file_path in sorted(results):
            # Use relative path from base directory for easier comparison
            rel_path = os.path.relpath(file_path, dir_path)
            f.write(rel_path + '\n')
    safe_print('   [INFO] Python scanned files list saved to: %s\n' % files_list_path)

    return results


def generate_markdown_report(all_issues, total_conversation_turns, total_problematic_turns):
    """生成Markdown报告"""
    now = datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%S.') + \
        str(datetime.utcnow().microsecond).zfill(6) + 'Z'

    markdown = '# OpenClaw Session Transcript 综合错误检测报告\n\n'
    markdown += '**生成时间**: %s\n\n' % now

    # 统计概览
    stats = {
        'total': len(all_issues),
        'byType': {},
    }

    for issue in all_issues:
        error_type = issue['errorType']
        stats['byType'][error_type] = stats['byType'].get(error_type, 0) + 1

    markdown += '## 📊 统计概览\n\n'
    markdown += '- **总错误数**: %d\n' % stats['total']
    markdown += '- **总对话轮数**: %d （排除系统消息）\n' % total_conversation_turns
    markdown += '- **有错误轮数**: %d （存在任何类型错误的轮次）\n' % total_problematic_turns
    if total_conversation_turns > 0:
        problem_rate = '%.2f' % (
            (float(total_problematic_turns) / total_conversation_turns) * 100)
        markdown += '- **错误率**: %s%% （有错误轮数 / 总对话轮数）\n' % problem_rate

    markdown += '\n'

    markdown += '### 错误类型分布\n\n'
    markdown += '| 错误类型 | 数量 | 说明 |\n'
    markdown += '|---------|------|------|\n'

    type_descriptions = {
        'flow_integrity_no_reply': '用户提问后无回复',
        'flow_integrity_missing_tool_result': '工具调用后无执行结果',
        'flow_integrity_missing_final_answer': '工具执行后无最终回复',
        'modelErrors': '模型API错误',
        'timeoutErrors': '超时错误',
        'rateLimitErrors': '速率限制错误',
        'toolErrors': '工具执行错误',
        'permissionErrors': '权限错误',
        'parsingErrors': '解析错误',
        'networkErrors': '网络错误',
        'abnormal_stop': '异常停止',
    }

    sorted_types = sorted(stats['byType'].items(),
                          key=lambda x: x[1], reverse=True)

    for error_type, count in sorted_types:
        desc = type_descriptions.get(error_type, error_type)
        markdown += '| %s | %d | %s |\n' % (error_type, count, desc)

    markdown += '\n---\n\n'

    # 按问题类型分组输出详细问题
    grouped_by_type = {}
    for issue in all_issues:
        error_type = issue['errorType']
        if error_type not in grouped_by_type:
            grouped_by_type[error_type] = []
        grouped_by_type[error_type].append(issue)

    type_descriptions_long = {
        'flow_integrity_no_reply': '用户提问后无回复',
        'flow_integrity_missing_tool_result': '工具调用后无执行结果',
        'flow_integrity_missing_final_answer': '工具执行后无最终回复',
        'modelErrors': '模型API错误',
        'timeoutErrors': '超时错误',
        'rateLimitErrors': '速率限制错误',
        'toolErrors': '工具执行错误',
        'permissionErrors': '权限错误',
        'parsingErrors': '解析错误',
        'networkErrors': '网络错误',
        'abnormal_stop': '异常停止',
    }

    sorted_groups = sorted(grouped_by_type.items(),
                           key=lambda x: len(x[1]), reverse=True)

    global_issue_number = 1

    for error_type, issues in sorted_groups:
        type_desc = type_descriptions_long.get(error_type, error_type)
        markdown += '## %s - %s (%d)\n\n' % (error_type,
                                             type_desc, len(issues))

        for issue in issues:
            markdown += '### 错误 #%d\n\n' % global_issue_number
            global_issue_number += 1
            markdown += '- **事件类型**: `%s`\n' % issue['eventType']
            markdown += '- **描述**: %s\n' % issue['description']

            # 添加员工信息（如果有）
            if issue.get('employee'):
                emp = issue['employee']
                markdown += '- **姓名**: %s\n' % emp['name']
                markdown += '- **工号**: %s\n' % emp['employee_id']
                markdown += '- **部门**: %s\n' % emp['department']

            # 所有类型问题都添加用户输入
            if issue.get('userInput'):
                user_input = issue['userInput']
                truncated_input = user_input[:200] + \
                    '...' if len(user_input) > 200 else user_input
                escaped_input = truncated_input.replace('`', '\\`')
                markdown += '- **用户输入**: `%s`\n' % escaped_input

            markdown += '- **错误信息**: \n````\n%s\n````\n' % issue['errorMessage']
            
            # 如果存在完整行记录（针对list index out of range错误），显示它
            if issue.get('fullLineRecord'):
                markdown += '- **完整行记录**: \n```json\n%s\n```\n' % issue['fullLineRecord']
            
            markdown += '- **原因分析**: %s\n' % issue['causeAnalysis']
            markdown += '- **文件位置**: `%s`\n' % issue['filePath'].replace(
                os.getcwd() + os.sep, '')
            markdown += '- **Session ID**: `%s`\n' % issue['sessionId']
            markdown += '- **行号**: %d\n' % issue['lineNumber']

            if issue.get('timestamp'):
                markdown += '- **时间戳**: %s\n' % issue['timestamp']

            if issue.get('runId'):
                markdown += '- **Run ID**: `%s`\n' % issue['runId']

            markdown += '\n---\n\n'

    return markdown


def parse_time_argument(time_str):
    """
    解析时间参数字符串
    
    支持的格式：
    - YYYY-MM-DD (例如: 2026-04-01)
    - YYYY-MM-DDTHH:MM:SS (例如: 2026-04-01T10:30:00)
    - YYYY-MM-DD HH:MM:SS (例如: 2026-04-01 10:30:00)
    
    Returns:
        datetime 对象，如果解析失败返回 None
    """
    if not time_str:
        return None
    
    formats = [
        '%Y-%m-%dT%H:%M:%S',
        '%Y-%m-%d %H:%M:%S',
        '%Y-%m-%d',
    ]
    
    for fmt in formats:
        try:
            return datetime.strptime(time_str, fmt)
        except ValueError:
            continue
    
    safe_print('⚠️ 无法解析时间格式: %s' % time_str)
    safe_print('   支持的格式: YYYY-MM-DD, YYYY-MM-DDTHH:MM:SS, YYYY-MM-DD HH:MM:SS')
    return None


def extract_time_from_filename(filename):
    """
    从文件名中提取时间戳
    
    支持的格式：
    - .reset.YYYY-MM-DDTHH-MM-SS.mmmZ
    - .deleted.YYYY-MM-DDTHH-MM-SS.mmmZ
    - .compacted.YYYY-MM-DDTHH-MM-SS.mmmZ
    
    Returns:
        datetime 对象，如果提取失败返回 None
    """
    # 匹配归档文件中的时间戳
    patterns = [
        r'\.(?:reset|deleted|compacted)\.(\d{4})-(\d{2})-(\d{2})T(\d{2})-(\d{2})-(\d{2})',
    ]
    
    import re
    for pattern in patterns:
        match = re.search(pattern, filename)
        if match:
            # 直接提取各个部分并重组
            year, month, day, hour, minute, second = match.groups()
            time_str = '%s-%s-%sT%s:%s:%s' % (year, month, day, hour, minute, second)
            try:
                return datetime.strptime(time_str, '%Y-%m-%dT%H:%M:%S')
            except ValueError:
                continue
    
    return None


def get_file_time_safe(file_path):
    """
    安全地获取文件时间，处理 Windows 长路径问题
    
    Args:
        file_path: 文件路径
    
    Returns:
        datetime 对象，如果失败返回 None
    """
    try:
        # Windows 长路径处理：添加 UNC 前缀
        if sys.platform == 'win32':
            # 总是使用 UNC 前缀来处理长路径
            abs_path = os.path.abspath(file_path)
            if not abs_path.startswith('\\\\?\\'):
                unc_path = '\\\\?\\' + abs_path
            else:
                unc_path = abs_path
            
            # 获取文件创建时间
            file_time = datetime.fromtimestamp(os.path.getctime(unc_path))
        else:
            # Linux/Mac 使用 st_mtime（最后修改时间）
            file_time = datetime.fromtimestamp(os.path.getmtime(file_path))
        
        return file_time
    except Exception as e:
        # 静默失败，返回 None
        return None


def filter_files_by_time(files, start_time=None, end_time=None):
    """
    根据文件创建时间过滤文件列表
    
    Args:
        files: 文件路径列表
        start_time: 起始时间（datetime对象），只保留此时间之后创建的文件
        end_time: 结束时间（datetime对象），只保留此时间之前创建的文件
    
    Returns:
        过滤后的文件列表
    """
    if not start_time and not end_time:
        return files
    
    filtered = []
    skipped_count = 0
    from_filename_count = 0
    
    for file_path in files:
        file_time = None
        
        # 首先尝试从文件系统获取文件时间
        # 注意：即使 os.path.exists() 可能因长路径失败，我们也尝试获取时间
        file_time = get_file_time_safe(file_path)
        
        # 如果文件系统获取失败，尝试从文件名提取时间
        if file_time is None:
            filename = os.path.basename(file_path)
            file_time = extract_time_from_filename(filename)
            if file_time is not None:
                from_filename_count += 1
        
        # 如果仍然无法获取时间，保留文件（不过滤）
        if file_time is None:
            filtered.append(file_path)
            continue
        
        # 检查是否在时间范围内
        if start_time and file_time < start_time:
            skipped_count += 1
            continue
        
        if end_time and file_time > end_time:
            skipped_count += 1
            continue
        
        filtered.append(file_path)
    
    # 显示统计信息
    if from_filename_count > 0:
        safe_print('   ℹ️  从文件名提取时间的文件数: %d' % from_filename_count)
    if skipped_count > 0:
        safe_print('   ℹ️  根据时间范围过滤：跳过 %d 个文件，保留 %d 个文件\n' % (
            skipped_count, len(filtered)))
    
    return filtered


def main():
    """主函数"""
    args = sys.argv[1:]
    
    # 解析命令行参数
    custom_dir = None
    start_time_str = None
    end_time_str = None
    
    i = 0
    while i < len(args):
        if args[i] in ['--start-time', '--start']:
            if i + 1 < len(args):
                start_time_str = args[i + 1]
                i += 2
            else:
                safe_print('❌ 错误: --start-time 需要指定时间参数')
                sys.exit(1)
        elif args[i] in ['--end-time', '--end']:
            if i + 1 < len(args):
                end_time_str = args[i + 1]
                i += 2
            else:
                safe_print('❌ 错误: --end-time 需要指定时间参数')
                sys.exit(1)
        elif not args[i].startswith('--'):
            # 第一个非选项参数作为目录路径
            if custom_dir is None:
                custom_dir = args[i]
            i += 1
        else:
            safe_print('⚠️ 未知参数: %s' % args[i])
            i += 1
    
    # 解析时间参数
    start_time = parse_time_argument(start_time_str)
    end_time = parse_time_argument(end_time_str)
    
    if start_time:
        safe_print('📅 起始时间: %s' % start_time.strftime('%Y-%m-%d %H:%M:%S'))
    if end_time:
        safe_print('📅 结束时间: %s' % end_time.strftime('%Y-%m-%d %H:%M:%S'))
    if start_time or end_time:
        safe_print('')
    
    if custom_dir:
        if os.path.isabs(custom_dir):
            transcript_dir = custom_dir
        else:
            transcript_dir = os.path.join(os.getcwd(), custom_dir)
        safe_print('[INFO] Using custom path: %s\n' % transcript_dir)
    else:
        script_dir = os.path.dirname(os.path.abspath(__file__))
        transcript_dir = os.path.join(
            script_dir, '..', 'logs', 'session-transcript', 'openclaw-logs')
        safe_print('[INFO] Using default path: %s\n' % transcript_dir)

    if not os.path.exists(transcript_dir):
        safe_print('❌ Transcript directory not found: %s' % transcript_dir)
        sys.exit(1)

    safe_print('\n🔍 开始扫描transcript文件...')
    jsonl_files = find_jsonl_files(transcript_dir)
    
    # 根据时间范围过滤文件
    if start_time or end_time:
        safe_print('\n⏰ 正在根据时间范围过滤文件...')
        jsonl_files = filter_files_by_time(jsonl_files, start_time, end_time)
        safe_print('✅ 过滤后剩余 %d 个文件\n' % len(jsonl_files))

    # 加载账户映射
    script_dir = os.path.dirname(os.path.abspath(__file__))
    accounts_mapping = load_accounts_mapping(script_dir)

    all_issues = []
    total_conversation_turns = 0  # 总对话轮数
    total_problematic_turns = 0   # 总有问题轮数

    safe_print('\n🔬 开始分析文件...\n')
    import time
    start_time = time.time()

    for i, file_path in enumerate(jsonl_files):
        result = analyze_transcript(file_path, accounts_mapping)
        all_issues.extend(result['issues'])
        total_conversation_turns += result['conversationTurns']
        total_problematic_turns += result['problematicTurns']

        # 每处理 10 个文件显示一次进度（更频繁）
        if (i + 1) % 10 == 0 or (i + 1) == len(jsonl_files):
            elapsed = time.time() - start_time
            progress = (i + 1) / float(len(jsonl_files))
            if progress > 0:
                estimated_total = elapsed / progress
                remaining = estimated_total - elapsed
                safe_print('   进度: %d/%d (%.1f%%), 发现错误 %d 个, 预计剩余 %.0f 秒...' % (
                    i + 1, len(jsonl_files), progress * 100, len(all_issues), remaining))

    elapsed_time = time.time() - start_time
    safe_print('\n✅ 分析完成！共发现 %d 个错误 (耗时 %.1f 秒)\n' %
               (len(all_issues), elapsed_time))

    script_dir = os.path.dirname(os.path.abspath(__file__))
    report_path = os.path.join(
        script_dir, 'transcript-comprehensive-issues.md')
    report = generate_markdown_report(
        all_issues, total_conversation_turns, total_problematic_turns)

    with open(report_path, 'w') as f:
        f.write(report)

    safe_print('📄 报告已保存到: %s\n' % report_path)

    # 输出统计信息
    stats = {}
    for issue in all_issues:
        error_type = issue['errorType']
        stats[error_type] = stats.get(error_type, 0) + 1

    safe_print('📊 错误类型统计:')
    for error_type, count in sorted(stats.items(), key=lambda x: x[1], reverse=True):
        safe_print('  - %s: %d' % (error_type, count))

    safe_print('\n🎯 严重程度统计:')
    severity_stats = {}
    for issue in all_issues:
        severity = issue.get('severity', 'UNKNOWN')
        severity_stats[severity] = severity_stats.get(severity, 0) + 1

    for severity in ['HIGH', 'MEDIUM', 'LOW']:
        if severity in severity_stats:
            safe_print('  - %s: %d' % (severity, severity_stats[severity]))


if __name__ == '__main__':
    main()
