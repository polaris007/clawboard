package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardExecutionTrace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 执行链路追踪 Mapper
 */
@Mapper
public interface ExecutionTraceMapper {
    
    /**
     * 批量插入执行链路节点（忽略重复）
     * @param nodes 节点列表
     * @return 插入的记录数
     */
    int batchInsertIgnore(@Param("nodes") List<DashboardExecutionTrace> nodes);
    
    /**
     * 根据 turnId 查询执行链路
     * @param turnId 轮次ID
     * @return 节点列表（按 node_index 排序）
     */
    List<DashboardExecutionTrace> selectByTurnId(@Param("turnId") Long turnId);
    
    /**
     * 根据 scanId 删除执行链路（用于重置扫描）
     * @param scanId 扫描ID
     * @return 删除的记录数
     */
    int deleteByScanId(@Param("scanId") Long scanId);
}
