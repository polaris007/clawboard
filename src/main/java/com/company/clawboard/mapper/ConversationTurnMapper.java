package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardConversationTurn;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ConversationTurnMapper {
    int upsertTurn(DashboardConversationTurn turn);
    int batchInsertIgnore(List<DashboardConversationTurn> turns);
    DashboardConversationTurn selectById(Long id);
    List<DashboardConversationTurn> selectBySessionId(String sessionId);
}
