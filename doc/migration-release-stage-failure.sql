-- 为发布阶段记录失败原因；首次部署执行一次
ALTER TABLE t_release_stage ADD COLUMN failure_reason text NULL;
