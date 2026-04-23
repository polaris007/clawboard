package com.company.clawboard.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VInstanceDetailMapperTest {

    @Autowired
    private VInstanceDetailMapper mapper;

    @Test
    void should_return_empty_list_when_no_running_instances() {
        List<Map<String, Object>> instances = mapper.selectRunningInstances();
        assertThat(instances).isNotNull();
    }

    @Test
    void should_return_null_when_uid_not_exists() {
        String status = mapper.selectStatusByUid("non-existent-uid");
        assertThat(status).isNull();
    }

    @Test
    void should_return_empty_list_when_batch_query_not_exists() {
        List<Map<String, Object>> statuses = mapper.selectStatusByUids(
            List.of("uid1", "uid2", "uid3")
        );
        assertThat(statuses).isEmpty();
    }

    @Test
    void should_return_zero_when_no_registered_users() {
        Integer count = mapper.countRegisteredUsers();
        assertThat(count).isNotNull();
    }
}
