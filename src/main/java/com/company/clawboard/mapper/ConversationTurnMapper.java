package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardConversationTurn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
     * @param startTime 开始时间，格式 YYYY-MM-DD HH:mm:ss
     * @param endTime 结束时间，格式 YYYY-MM-DD HH:mm:ss
     * @return 对话轮次列表
     */
    List<DashboardConversationTurn> selectTurnsWithFilters(
        @Param("userId") String userId,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime
    );
}
