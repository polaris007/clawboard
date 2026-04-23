package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MessageMapper {
    int batchInsertIgnore(List<DashboardMessage> messages);
    
    /**
     * 根据 Session ID 统计消息数量（用于检查是否已扫描）
     */
    int countBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 根据轮次 ID 查询消息列表
     */
    List<DashboardMessage> selectByTurnId(@Param("turnId") Long turnId);
}
