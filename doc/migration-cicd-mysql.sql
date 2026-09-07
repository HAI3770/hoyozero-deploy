-- CI/CD 增量迁移；在已有 hoyozero_deploy 数据库执行一次
-- 首次安装执行以下 ALTER；已有环境若字段已存在请跳过对应字段
ALTER TABLE t_project
  ADD COLUMN dockerfile_path varchar(255) DEFAULT 'Dockerfile', ADD COLUMN docker_context varchar(255) DEFAULT '.',
  ADD COLUMN registry_url varchar(255), ADD COLUMN registry_username varchar(255), ADD COLUMN registry_token text,
  ADD COLUMN registry_namespace varchar(255), ADD COLUMN image_name varchar(255), ADD COLUMN image_tag_rule varchar(500),
  ADD COLUMN build_platform varchar(100) DEFAULT 'linux/amd64', ADD COLUMN auto_push tinyint DEFAULT 1, ADD COLUMN webhook_token varchar(255),
  ADD COLUMN deploy_enabled tinyint DEFAULT 0, ADD COLUMN compose_path varchar(500), ADD COLUMN compose_service varchar(255), ADD COLUMN health_check_url varchar(500);
ALTER TABLE t_build
  ADD COLUMN build_number int, ADD COLUMN git_url varchar(500), ADD COLUMN git_branch varchar(255), ADD COLUMN git_commit varchar(100),
  ADD COLUMN image varchar(500), ADD COLUMN image_tag varchar(255), ADD COLUMN image_digest varchar(255), ADD COLUMN stage varchar(50),
  ADD COLUMN runner_name varchar(100), ADD COLUMN failure_reason text;
CREATE INDEX idx_build_project_status ON t_build(project_id,status);
CREATE TABLE IF NOT EXISTS t_deployment (id bigint PRIMARY KEY AUTO_INCREMENT, project_id bigint NOT NULL, environment varchar(50), server_id bigint NOT NULL, service_name varchar(255), old_image varchar(500), new_image varchar(500), image_digest varchar(255), status varchar(30), deployment_log longtext, operate_by varchar(50), deployment_time datetime DEFAULT CURRENT_TIMESTAMP, INDEX idx_deploy_project(project_id));
CREATE TABLE IF NOT EXISTS t_runner_queue (id bigint PRIMARY KEY AUTO_INCREMENT, build_id bigint NOT NULL UNIQUE, status varchar(20) NOT NULL, runner_name varchar(100), create_time datetime DEFAULT CURRENT_TIMESTAMP, claim_time datetime, finish_time datetime, INDEX idx_runner_queue_status(status,create_time));
CREATE TABLE IF NOT EXISTS t_release (id bigint PRIMARY KEY AUTO_INCREMENT, project_id bigint NOT NULL, build_id bigint, environment varchar(50), status varchar(30) NOT NULL, target_image varchar(500), trigger_by varchar(50), failure_reason text, start_time datetime, end_time datetime, create_time datetime DEFAULT CURRENT_TIMESTAMP, INDEX idx_release_project_status(project_id,status), INDEX idx_release_build(build_id));
CREATE TABLE IF NOT EXISTS t_release_stage (id bigint PRIMARY KEY AUTO_INCREMENT, release_id bigint NOT NULL, stage_key varchar(50) NOT NULL, stage_name varchar(100) NOT NULL, stage_order int NOT NULL, status varchar(30) NOT NULL, log longtext, start_time datetime, end_time datetime, INDEX idx_release_stage_release(release_id,stage_order));
CREATE TABLE IF NOT EXISTS t_release_task (id bigint PRIMARY KEY AUTO_INCREMENT, release_id bigint NOT NULL, stage_id bigint, server_id bigint, rollback_build_id bigint, task_type varchar(50) NOT NULL, status varchar(30) NOT NULL, attempt int DEFAULT 0, log longtext, failure_reason text, start_time datetime, end_time datetime, INDEX idx_release_task_release(release_id), INDEX idx_release_task_status(status), INDEX idx_release_task_rollback_build(rollback_build_id));
