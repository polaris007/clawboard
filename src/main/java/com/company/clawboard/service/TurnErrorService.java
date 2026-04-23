package com.company.clawboard.service;

import com.company.clawboard.dto.*;
import com.company.clawboard.entity.DashboardConversationTurn;
import com.company.clawboard.entity.DashboardTranscriptIssue;
import com.company.clawboard.mapper.ConversationTurnMapper;
import com.company.clawboard.mapper.TranscriptIssueMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.time.ZoneId;

@Service
// @RequiredArgsConstructor
public class TurnErrorService {

    // 北京时区
    private static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");

    private final ConversationTurnMapper turnMapper;
    private final TranscriptIssueMapper issueMapper;

    public TurnErrorService(ConversationTurnMapper turnMapper, TranscriptIssueMapper issueMapper) {
        this.turnMapper = turnMapper;
        this.issueMapper = issueMapper;
    }

    public PageResult<TurnSearchItem> searchTurns(TurnSearchRequest request) {
        // 开启分页（会自动在SQL中添加LIMIT/OFFSET，并执行COUNT查询）
        PageHelper.startPage(request.getPageOrDefault(), request.getPageSizeOrDefault());
        
        // 直接使用字符串格式日期
        String startTimeStr = request.getStartTime();
        String endTimeStr = request.getEndTime();
        
        // 根据条件查询非系统轮次
        List<DashboardConversationTurn> turns = turnMapper.selectTurnsWithFilters(
            request.getUserId(),
            request.getUserIdLike(),
            startTimeStr,
            endTimeStr
        );
        
        // 获取分页信息
        PageInfo<DashboardConversationTurn> pageInfo = new PageInfo<>(turns);
        
        // 转换为响应格式
        List<TurnSearchItem> items = pageInfo.getList().stream().map(turn -> {
            var item = new TurnSearchItem();
            item.setTurnId(turn.getId());
            if (turn.getStartTime() != null) {
                item.setTimeStamp(turn.getStartTime().toInstant(BEIJING_ZONE.getRules().getOffset(turn.getStartTime())).toEpochMilli());
            } else {
                item.setTimeStamp(0L);
            }
            item.setUserName(turn.getEmployeeId());
            item.setUserInput(turn.getUserInput());
            item.setDurationMs(turn.getTotalDurationMs());
            // 将 String 类型的 status 转换为 Boolean：complete=true, 其他=false
            item.setResultStatus("complete".equals(turn.getStatus()));
            item.setQualityStatus(turn.getQualityStatus());
            
            var tokens = new TurnSearchItem.TokenInfo();
            tokens.setTotal(turn.getTotalTokens());
            tokens.setInput(turn.getTotalInputTokens());
            tokens.setOutput(turn.getTotalOutputTokens());
            item.setTokens(tokens);
            
            // 技能和工具需要从其他表查询，暂时返回空列表
            item.setSkills(List.of());
            item.setTools(List.of());
            // 只返回文件名，不含路径
            if (turn.getLogFilePath() != null) {
                String logFilePath = turn.getLogFilePath();
                int lastSlashIndex = logFilePath.lastIndexOf('/');
                int lastBackslashIndex = logFilePath.lastIndexOf('\\');
                int lastSeparatorIndex = Math.max(lastSlashIndex, lastBackslashIndex);
                if (lastSeparatorIndex != -1) {
                    item.setLogFileName(logFilePath.substring(lastSeparatorIndex + 1));
                } else {
                    item.setLogFileName(logFilePath);
                }
            } else {
                item.setLogFileName("");
            }
            
            return item;
        }).collect(Collectors.toList());
        
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), items);
    }

    public TraceResponse getTrace(Long turnId) {
        // 从 conversation_turn 表查询
        DashboardConversationTurn turn = turnMapper.selectById(turnId);
        var response = new TraceResponse();
        response.setTurnId(turnId);
        
        // 如果没有数据，返回空节点列表
        if (turn == null) {
            response.setNodes(List.of());
            return response;
        }
        
        // 构建简单的执行链路
        var nodes = List.of(
            createTraceNode(0, "user_input", "用户输入", true, turn.getStartTime()),
            createTraceNode(1, "skill_call", "技能调用", true, turn.getStartTime()),
            createTraceNode(2, "tool_call", "工具调用", true, turn.getStartTime()),
            createTraceNode(3, "reply", "回复用户", "success".equals(turn.getStatus()), turn.getEndTime())
        );
        response.setNodes(nodes);
        return response;
    }

    private TraceNode createTraceNode(int stepOrder, String nodeType, String nodeName, boolean status, java.time.LocalDateTime time) {
        var node = new TraceNode();
        node.setStepOrder(stepOrder);
        node.setNodeType(nodeType);
        node.setNodeName(nodeName);
        node.setStatus(status);
        if (time != null) {
            node.setTimeStamp(time.toInstant(java.time.ZoneOffset.UTC).toEpochMilli());
        } else {
            node.setTimeStamp(0L);
        }
        return node;
    }

    public ErrorSummaryResponse getErrorSummary(TimeRangeRequest request) {
        // 从 transcript_issue 表查询数据
        List<DashboardTranscriptIssue> issues = issueMapper.selectAll();
        var response = new ErrorSummaryResponse();
        response.setTotalErrors(issues.size());
        // 这里可以根据需要统计不同类型的错误
        return response;
    }

    public PageResult<ErrorSearchItem> searchErrors(ErrorSearchRequest request) {
        // 开启分页
        PageHelper.startPage(request.getPageOrDefault(), request.getPageSizeOrDefault());
        
        // 从 transcript_issue 表查询数据
        List<DashboardTranscriptIssue> issues = issueMapper.selectAll();
        
        // 获取分页信息
        PageInfo<DashboardTranscriptIssue> pageInfo = new PageInfo<>(issues);
        
        // 转换为响应格式
        List<ErrorSearchItem> items = pageInfo.getList().stream().map(issue -> {
            var item = new ErrorSearchItem();
            item.setId(issue.getId());
            item.setErrorType(issue.getErrorType());
            item.setSeverity(issue.getSeverity());
            item.setDescription(issue.getDescription());
            item.setErrorMessage(issue.getErrorMessage());
            item.setEmployeeName(issue.getEmployeeId());
            item.setOccurredAt(issue.getOccurredAt());
            item.setTurnId(issue.getTurnId());
            return item;
        }).collect(Collectors.toList());
        
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), items);
    }
}
