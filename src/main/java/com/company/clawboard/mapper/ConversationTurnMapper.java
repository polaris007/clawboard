package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardConversationTurn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ConversationTurnMapper {
    int upsertTurn(DashboardConversationTurn turn);
    int batchInsertIgnore(List<DashboardConversationTurn> turns);
    DashboardConversationTurn selectById(Long id);
    List<DashboardConversationTurn> selectBySessionId(String sessionId);
    List<DashboardConversationTurn> selectAll();
    
    /**
     * 查询非系统轮次（用于分页）
     */
    List<DashboardConversationTurn> selectNonSystemTurns();
    
    /**
     * 根据条件查询非系统轮次（支持分页）
     * @param userId 工号（employee_id）精确匹配
     * @param userIdLike 工号模糊匹配（employee_id LIKE '%xxx%'），userId 优先
     * @param startTime 开始时间，格式 YYYY-MM-DD HH:mm:ss
     * @param endTime 结束时间，格式 YYYY-MM-DD HH:mm:ss
     * @return 对话轮次列表
     */
    List<DashboardConversationTurn> selectTurnsWithFilters(
        @Param("userId") String userId,
        @Param("userIdLike") String userIdLike,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime
    );

    /**
     * 统计总非系统轮次数
     * @param teamName 团队名称（机构号）
     * @param userId 工号（employee_id）精确匹配
     * @param startTime 开始时间，格式 YYYY-MM-DD HH:mm:ss
     * @param endTime 结束时间，格式 YYYY-MM-DD HH:mm:ss
     * @return 总非系统轮次数
     */
    Integer countNonSystemTurns(
        @Param("teamName") String teamName,
        @Param("userId") String userId,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime
    );

    /**
     * 统计没有错误的完成轮次数（排除系统轮次）
     * @param teamName 团队名称（机构号）
     * @param userId 工号（employee_id）精确匹配
     * @param startTime 开始时间，格式 YYYY-MM-DD HH:mm:ss
     * @param endTime 结束时间，格式 YYYY-MM-DD HH:mm:ss
     * @return 没有错误的完成轮次数
     */
    Integer countNoErrorCompleteTurns(
        @Param("teamName") String teamName,
        @Param("userId") String userId,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime
    );

    /**
     * 根据时间范围查询对话轮次
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 对话轮次列表，按 start_time 升序排列
     */
    List<DashboardConversationTurn> selectTurnsByTimeRange(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    /**
     * 统计指定扫描ID的非系统轮次数
     * @param scanId 扫描ID
     * @return 非系统轮次数
     */
    Integer countNonSystemTurnsByScanId(@Param("scanId") Long scanId);
    
    /**
     * 统计指定扫描ID的有错误非系统轮次数
     * @param scanId 扫描ID
     * @return 有错误非系统轮次数（has_error = 1 AND system_turn = 0）
     */
    Integer countProblematicTurnsByScanId(@Param("scanId") Long scanId);
    
    /**
     * 统计时间范围内的有错误非系统轮次数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 有错误非系统轮次数（has_error = 1 AND system_turn = 0）
     */
    Integer countProblematicTurnsByTimeRange(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
    
    /**
     * 删除指定 session 的所有对话轮次
     */
    int deleteBySessionId(@Param("sessionId") String sessionId);
}
