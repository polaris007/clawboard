package com.company.clawboard.mapper;

import com.company.clawboard.entity.DashboardMessage;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MessageMapper {
    int batchInsertIgnore(List<DashboardMessage> messages);
}
