-- 1. 先创建不带索引的基础表结构
CREATE TABLE IF NOT EXISTS openclaw_instances (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    uid VARCHAR(64) NOT NULL COMMENT '用户ID',
    instance_name VARCHAR(128) NOT NULL DEFAULT '' COMMENT '实例名称',
    namespace VARCHAR(64) DEFAULT 'default' COMMENT 'K8s命名空间',
    base_url VARCHAR(512) COMMENT 'API基础URL',
    node_port INT COMMENT '分配的NodePort端口',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    access_url VARCHAR(512) COMMENT '访问地址',
    encrypted_token VARCHAR(256) COMMENT '加密的认证token',
    status VARCHAR(32) DEFAULT 'creating' COMMENT '状态: creating, running, error, deleting, deleted',
    current_step VARCHAR(64) COMMENT '当前步骤',
    progress INT DEFAULT 0 COMMENT '进度百分比 0-100',
    pod_name VARCHAR(128) COMMENT '对应的Pod名称',
    deployment_ready BOOLEAN DEFAULT FALSE COMMENT 'Deployment是否就绪',
    service_ready BOOLEAN DEFAULT FALSE COMMENT 'Service是否就绪',
    pod_phase VARCHAR(32) COMMENT 'Pod状态Phase',
    pod_conditions JSON COMMENT 'Pod详细条件',
    config_json JSON COMMENT 'ConfigMap配置内容',
    deployment_json JSON COMMENT 'Deployment配置内容',
    service_json JSON COMMENT 'Service配置内容',
    user_config_json JSON COMMENT '用户配置信息',
    pod_json JSON COMMENT 'Pod配置内容（预留字段）',
    estimated_ready_time TIMESTAMP NULL COMMENT '预估就绪时间',
    last_event_time TIMESTAMP NULL COMMENT '最后事件时间',
    last_status_check TIMESTAMP NULL COMMENT '最后状态检查时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '逻辑删除时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OpenClaw实例管理表';

-- 2. 然后分别添加索引
ALTER TABLE openclaw_instances ADD INDEX idx_uid (uid);
ALTER TABLE openclaw_instances ADD INDEX idx_status (status);
ALTER TABLE openclaw_instances ADD INDEX idx_created_at (created_at);
ALTER TABLE openclaw_instances ADD INDEX idx_deleted_at (deleted_at);
ALTER TABLE openclaw_instances ADD UNIQUE KEY uk_instance_name_namespace (instance_name, namespace);

-- view
create or replace
view `v_instance_detail` as
select
    `openclaw_instances`.`id` as `id`,
    `openclaw_instances`.`uid` as `uid`,
    `openclaw_instances`.`instance_name` as `instance_name`,
    `openclaw_instances`.`namespace` as `namespace`,
    `openclaw_instances`.`node_port` as `node_port`,
    `openclaw_instances`.`access_url` as `access_url`,
    `openclaw_instances`.`encrypted_token` as `encrypted_token`,
    `openclaw_instances`.`status` as `status`,
    `openclaw_instances`.`created_at` as `created_at`,
    `openclaw_instances`.`updated_at` as `updated_at`,
    JSON_UNQUOTE(JSON_EXTRACT(`openclaw_instances`.`user_config_json`, '$.userName')) as `user_config_name`,
    JSON_UNQUOTE(JSON_EXTRACT(`openclaw_instances`.`user_config_json`, '$.orgCode')) as `user_config_org_code`,
    JSON_UNQUOTE(JSON_EXTRACT(`openclaw_instances`.`user_config_json`, '$.kmBaseUrl')) as `user_config_km_base_url`,
    JSON_UNQUOTE(JSON_EXTRACT(`openclaw_instances`.`deployment_json`, '$.spec.template.spec.containers[0].image')) as `image`,
    JSON_UNQUOTE(JSON_EXTRACT(`openclaw_instances`.`deployment_json`, '$.spec.template.spec.containers[0].securityContext.runAsUser')) as `usernum`,
    IFNULL(JSON_UNQUOTE(JSON_EXTRACT(`openclaw_instances`.`deployment_json`, replace(JSON_UNQUOTE(JSON_SEARCH(`openclaw_instances`.`deployment_json`, 'one', 'openclaw', null, '$.spec.template.spec.volumes[*].name')), '.name', '.hostPath.path'))), null) as `openclaw_hostpath_path`
from
    `openclaw_instances`;
