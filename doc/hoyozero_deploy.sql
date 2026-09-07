-- hoyozero-deploy MySQL 8.0 baseline schema
-- This file intentionally contains no customer records, credentials, hosts, tokens or build logs.
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `t_user` (
  `id` bigint NOT NULL, `username` varchar(64) NOT NULL, `password` varchar(64) NOT NULL,
  `nickname` varchar(128) DEFAULT NULL, `email` varchar(128) DEFAULT NULL, `phone` varchar(32) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 1, `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`), UNIQUE KEY `uk_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_role` (
  `id` bigint NOT NULL, `name` varchar(64) NOT NULL, `code` varchar(64) NOT NULL, `description` varchar(255) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0, PRIMARY KEY (`id`), UNIQUE KEY `uk_role_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_menu` (
  `id` bigint NOT NULL, `parent_id` bigint NOT NULL DEFAULT 0, `name` varchar(64) NOT NULL, `path` varchar(255) NOT NULL,
  `icon` varchar(64) DEFAULT NULL, `sort_order` int NOT NULL DEFAULT 0, `menu_type` varchar(16) NOT NULL DEFAULT 'MENU',
  `permission` varchar(128) DEFAULT NULL, `visible` tinyint NOT NULL DEFAULT 1,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0, PRIMARY KEY (`id`), UNIQUE KEY `uk_menu_path` (`path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_user_role` (
  `id` bigint NOT NULL, `user_id` bigint NOT NULL, `role_id` bigint NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id`), UNIQUE KEY `uk_user_role` (`user_id`, `role_id`), KEY `idx_user_role_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_role_menu` (
  `id` bigint NOT NULL, `role_id` bigint NOT NULL, `menu_id` bigint NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id`), UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`), KEY `idx_role_menu_menu` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_project_group` (
  `id` bigint NOT NULL, `name` varchar(128) NOT NULL, `description` varchar(500) DEFAULT NULL, `owner` varchar(128) DEFAULT NULL, `owner_contact` varchar(128) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`), UNIQUE KEY `uk_project_group_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_server` (
  `id` bigint NOT NULL, `name` varchar(128) NOT NULL, `host` varchar(255) NOT NULL, `port` int NOT NULL DEFAULT 22, `username` varchar(128) DEFAULT NULL,
  `auth_type` varchar(32) DEFAULT NULL, `password` text, `private_key` longtext, `upload_path` varchar(512) DEFAULT NULL, `status` varchar(32) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`), UNIQUE KEY `uk_server_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_project` (
  `id` bigint NOT NULL, `name` varchar(128) NOT NULL, `description` varchar(1000) DEFAULT NULL, `git_url` varchar(512) DEFAULT NULL, `branch` varchar(128) DEFAULT NULL,
  `git_username` varchar(128) DEFAULT NULL, `git_password` text, `project_type` varchar(32) DEFAULT NULL, `build_command` text, `build_dir` varchar(512) DEFAULT NULL,
  `project_dir` varchar(512) DEFAULT NULL, `server_id` bigint DEFAULT NULL, `auto_deploy` tinyint NOT NULL DEFAULT 0, `deploy_script` longtext, `deploy_path` varchar(512) DEFAULT NULL,
  `app_port` int DEFAULT NULL, `env` varchar(64) DEFAULT NULL, `group_id` bigint DEFAULT NULL, `dockerfile_path` varchar(512) DEFAULT NULL, `docker_context` varchar(512) DEFAULT NULL,
  `registry_url` varchar(512) DEFAULT NULL, `registry_username` varchar(128) DEFAULT NULL, `registry_token` text, `registry_namespace` varchar(256) DEFAULT NULL,
  `image_name` varchar(256) DEFAULT NULL, `image_tag_rule` varchar(256) DEFAULT NULL, `build_platform` varchar(64) DEFAULT NULL, `auto_push` tinyint NOT NULL DEFAULT 0,
  `webhook_token` varchar(255) DEFAULT NULL, `deploy_enabled` tinyint NOT NULL DEFAULT 0, `compose_path` varchar(512) DEFAULT NULL, `compose_service` varchar(128) DEFAULT NULL,
  `health_check_url` varchar(512) DEFAULT NULL, `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`), UNIQUE KEY `uk_project_name` (`name`), KEY `idx_project_group` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_project_member` (
  `id` bigint NOT NULL, `project_id` bigint NOT NULL, `user_id` bigint NOT NULL, `role_type` varchar(32) NOT NULL, `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`), UNIQUE KEY `uk_project_member` (`project_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_project_server` (
  `id` bigint NOT NULL, `project_id` bigint NOT NULL, `server_id` bigint NOT NULL, `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`), UNIQUE KEY `uk_project_server` (`project_id`, `server_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_build` (
  `id` bigint NOT NULL, `project_id` bigint NOT NULL, `status` varchar(32) NOT NULL DEFAULT 'PENDING', `log` longtext, `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL, `duration` bigint DEFAULT NULL, `trigger_by` varchar(64) DEFAULT NULL, `build_number` int DEFAULT NULL, `git_url` varchar(512) DEFAULT NULL,
  `git_branch` varchar(128) DEFAULT NULL, `git_commit` varchar(128) DEFAULT NULL, `image` varchar(512) DEFAULT NULL, `image_tag` varchar(256) DEFAULT NULL,
  `image_digest` varchar(256) DEFAULT NULL, `stage` varchar(128) DEFAULT NULL, `runner_name` varchar(128) DEFAULT NULL, `failure_reason` text,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`), KEY `idx_build_project` (`project_id`), KEY `idx_build_status` (`status`), KEY `idx_build_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_runner_queue` (
  `id` bigint NOT NULL AUTO_INCREMENT, `build_id` bigint NOT NULL, `status` varchar(32) NOT NULL DEFAULT 'PENDING', `runner_name` varchar(128) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, `claim_time` datetime DEFAULT NULL, `finish_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`), KEY `idx_runner_queue_status` (`status`), KEY `idx_runner_queue_build` (`build_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_release` (
  `id` bigint NOT NULL, `project_id` bigint DEFAULT NULL, `build_id` bigint DEFAULT NULL, `environment` varchar(64) DEFAULT NULL, `status` varchar(64) DEFAULT NULL,
  `target_image` varchar(512) DEFAULT NULL, `trigger_by` varchar(64) DEFAULT NULL, `failure_reason` text, `start_time` datetime DEFAULT NULL, `end_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id`),
  KEY `idx_release_project` (`project_id`), KEY `idx_release_build` (`build_id`), KEY `idx_release_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_release_stage` (
  `id` bigint NOT NULL, `release_id` bigint NOT NULL, `stage_key` varchar(64) NOT NULL, `stage_name` varchar(128) NOT NULL, `stage_order` int NOT NULL,
  `status` varchar(64) DEFAULT NULL, `failure_reason` text, `log` longtext, `start_time` datetime DEFAULT NULL, `end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`), KEY `idx_release_stage_release` (`release_id`), KEY `idx_release_stage_order` (`release_id`, `stage_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_release_task` (
  `id` bigint NOT NULL, `release_id` bigint NOT NULL, `stage_id` bigint DEFAULT NULL, `server_id` bigint DEFAULT NULL, `rollback_build_id` bigint DEFAULT NULL,
  `task_type` varchar(64) NOT NULL, `status` varchar(64) DEFAULT NULL, `attempt` int DEFAULT 0, `log` longtext, `failure_reason` text,
  `start_time` datetime DEFAULT NULL, `end_time` datetime DEFAULT NULL, PRIMARY KEY (`id`),
  KEY `idx_release_task_release` (`release_id`), KEY `idx_release_task_rollback` (`rollback_build_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_deployment` (
  `id` bigint NOT NULL, `project_id` bigint DEFAULT NULL, `environment` varchar(64) DEFAULT NULL, `server_id` bigint DEFAULT NULL, `service_name` varchar(128) DEFAULT NULL,
  `old_image` varchar(512) DEFAULT NULL, `new_image` varchar(512) DEFAULT NULL, `image_digest` varchar(256) DEFAULT NULL, `status` varchar(64) DEFAULT NULL,
  `deployment_log` longtext, `operate_by` varchar(64) DEFAULT NULL, `deployment_time` datetime DEFAULT NULL, PRIMARY KEY (`id`),
  KEY `idx_deployment_project` (`project_id`), KEY `idx_deployment_time` (`deployment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_plugin` (
  `id` bigint NOT NULL, `name` varchar(128) NOT NULL, `plugin_key` varchar(128) NOT NULL, `category` varchar(64) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL, `icon` varchar(128) DEFAULT NULL, `versions` text, `default_version` varchar(64) DEFAULT NULL,
  `install_script` longtext, `uninstall_script` longtext, `check_script` longtext, `sort` int NOT NULL DEFAULT 0, `status` varchar(32) NOT NULL DEFAULT 'ENABLED',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`), UNIQUE KEY `uk_plugin_key` (`plugin_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_plugin_install` (
  `id` bigint NOT NULL, `server_id` bigint NOT NULL, `plugin_id` bigint NOT NULL, `version` varchar(64) DEFAULT NULL, `status` varchar(32) NOT NULL,
  `log` longtext, `install_path` varchar(512) DEFAULT NULL, `install_time` datetime DEFAULT NULL, `operate_by` varchar(64) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT 0, PRIMARY KEY (`id`), KEY `idx_plugin_install_server` (`server_id`), KEY `idx_plugin_install_plugin` (`plugin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_login_log` (
  `id` bigint NOT NULL, `user_id` bigint DEFAULT NULL, `username` varchar(64) DEFAULT NULL, `ip_address` varchar(64) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL, `browser` varchar(128) DEFAULT NULL, `os` varchar(128) DEFAULT NULL, `status` tinyint NOT NULL,
  `message` varchar(500) DEFAULT NULL, `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id`), KEY `idx_login_log_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_operation_log` (
  `id` bigint NOT NULL, `user_id` bigint DEFAULT NULL, `username` varchar(64) DEFAULT NULL, `module` varchar(128) DEFAULT NULL,
  `operation_type` varchar(128) DEFAULT NULL, `description` varchar(500) DEFAULT NULL, `method` varchar(16) DEFAULT NULL, `params` longtext,
  `result` longtext, `ip_address` varchar(64) DEFAULT NULL, `location` varchar(255) DEFAULT NULL, `status` tinyint NOT NULL DEFAULT 1,
  `error_msg` longtext, `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id`), KEY `idx_operation_log_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `t_system_log` (
  `id` bigint NOT NULL, `user_id` bigint DEFAULT NULL, `username` varchar(64) DEFAULT NULL, `operation` varchar(255) DEFAULT NULL,
  `method` varchar(16) DEFAULT NULL, `params` longtext, `ip` varchar(64) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id`), KEY `idx_system_log_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `t_role` (`id`, `name`, `code`, `description`) VALUES
  (1, '平台管理员', 'ADMIN', '拥有平台全部管理权限'),
  (2, '项目开发者', 'DEVELOPER', '可参与项目构建与发布'),
  (3, '项目成员', 'MEMBER', '可查看已授权项目');

INSERT INTO `t_menu` (`id`, `parent_id`, `name`, `path`, `icon`, `sort_order`, `menu_type`, `permission`, `visible`) VALUES
  (1, 0, '运维总览', '/dashboard', 'SpeedometerSharp', 1, 'MENU', 'dashboard:view', 1),
  (2, 0, '项目管理', '/project', 'FolderOpenSharp', 2, 'MENU', 'project:view', 1),
  (3, 0, '项目分组', '/project-group', 'Albums', 3, 'MENU', 'project-group:view', 1),
  (4, 0, '构建记录', '/build', 'RocketSharp', 4, 'MENU', 'build:view', 1),
  (5, 0, '持续交付', '/release', 'RocketSharp', 5, 'MENU', 'release:view', 1),
  (6, 0, '服务器管理', '/server', 'ServerSharp', 6, 'MENU', 'server:view', 1),
  (7, 0, '文件管理', '/file', 'FolderOpenSharp', 7, 'MENU', 'file:view', 1),
  (8, 0, '用户管理', '/user', 'PeopleSharp', 8, 'MENU', 'user:view', 1),
  (9, 0, '角色管理', '/role', 'ShieldCheckmarkSharp', 9, 'MENU', 'role:view', 1),
  (10, 0, '菜单管理', '/menu', 'MenuSharp', 10, 'MENU', 'menu:view', 1),
  (11, 0, '插件市场', '/plugin', 'ExtensionPuzzleSharp', 11, 'MENU', 'plugin:view', 1),
  (12, 0, '操作日志', '/log/operation', 'DocumentTextSharp', 12, 'MENU', 'log:operation:view', 1),
  (13, 0, '登录日志', '/log/login', 'LogInSharp', 13, 'MENU', 'log:login:view', 1);

INSERT INTO `t_role_menu` (`id`, `role_id`, `menu_id`) VALUES
  (101, 1, 1), (102, 1, 2), (103, 1, 3), (104, 1, 4), (105, 1, 5), (106, 1, 6), (107, 1, 7),
  (108, 1, 8), (109, 1, 9), (110, 1, 10), (111, 1, 11), (112, 1, 12), (113, 1, 13);

SET FOREIGN_KEY_CHECKS = 1;
