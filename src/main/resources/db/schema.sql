-- SQLite Schema for hoyozero-deploy
-- 基于 MySQL 脚本转换

-- 服务器表
CREATE TABLE IF NOT EXISTS t_server (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    host TEXT NOT NULL,
    port INTEGER DEFAULT 22,
    username TEXT NOT NULL,
    auth_type TEXT DEFAULT 'PASSWORD',
    password TEXT,
    private_key TEXT,
    upload_path TEXT DEFAULT '/opt/deploy',
    start_cmd TEXT,
    stop_cmd TEXT,
    status TEXT DEFAULT 'OFFLINE',
    create_time TEXT DEFAULT CURRENT_TIMESTAMP,
    update_time TEXT DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 项目组表
CREATE TABLE IF NOT EXISTS t_project_group (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    owner TEXT,
    owner_contact TEXT,
    create_time TEXT DEFAULT CURRENT_TIMESTAMP,
    update_time TEXT DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 项目表
CREATE TABLE IF NOT EXISTS t_project (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    git_url TEXT NOT NULL,
    branch TEXT DEFAULT 'master',
    git_username TEXT,
    git_password TEXT,
    auto_deploy INTEGER DEFAULT 0,
    deploy_script TEXT,
    deploy_path TEXT DEFAULT '/home/deploy/',
    app_port INTEGER,
    project_type TEXT NOT NULL,
    build_command TEXT NOT NULL,
    build_dir TEXT NOT NULL,
    project_dir TEXT,
    server_id INTEGER,
    create_time TEXT DEFAULT CURRENT_TIMESTAMP,
    update_time TEXT DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0,
    env TEXT DEFAULT 'development',
    group_id INTEGER,
    FOREIGN KEY (server_id) REFERENCES t_server(id),
    FOREIGN KEY (group_id) REFERENCES t_project_group(id)
);

-- 项目环境索引
CREATE INDEX IF NOT EXISTS idx_project_env ON t_project(env);

-- 构建记录表
CREATE TABLE IF NOT EXISTS t_build (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    project_id INTEGER NOT NULL,
    status TEXT DEFAULT 'PENDING',
    log TEXT,
    start_time TEXT,
    end_time TEXT,
    duration INTEGER,
    trigger_by TEXT,
    create_time TEXT DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (project_id) REFERENCES t_project(id)
);

-- 构建记录项目ID索引
CREATE INDEX IF NOT EXISTS idx_build_project_id ON t_build(project_id);

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    nickname TEXT,
    email TEXT,
    phone TEXT,
    status INTEGER DEFAULT 1,
    create_time TEXT DEFAULT CURRENT_TIMESTAMP,
    update_time TEXT DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 角色表
CREATE TABLE IF NOT EXISTS t_role (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    code TEXT NOT NULL UNIQUE,
    description TEXT,
    create_time TEXT DEFAULT CURRENT_TIMESTAMP,
    update_time TEXT DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS t_user_role (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    create_time TEXT DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES t_role(id) ON DELETE CASCADE
);

-- 菜单表
CREATE TABLE IF NOT EXISTS t_menu (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    parent_id INTEGER DEFAULT 0,
    name TEXT NOT NULL,
    path TEXT,
    icon TEXT,
    sort_order INTEGER DEFAULT 0,
    menu_type TEXT DEFAULT 'MENU',
    permission TEXT,
    visible INTEGER DEFAULT 1,
    create_time TEXT DEFAULT CURRENT_TIMESTAMP,
    update_time TEXT DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS t_role_menu (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    role_id INTEGER NOT NULL,
    menu_id INTEGER NOT NULL,
    create_time TEXT DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(role_id, menu_id)
);

-- 登录日志表
CREATE TABLE IF NOT EXISTS t_login_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    username TEXT NOT NULL,
    ip_address TEXT,
    location TEXT,
    browser TEXT,
    os TEXT,
    status INTEGER NOT NULL DEFAULT 1,
    message TEXT,
    login_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 登录日志索引
CREATE INDEX IF NOT EXISTS idx_login_log_username ON t_login_log(username);
CREATE INDEX IF NOT EXISTS idx_login_log_user_id ON t_login_log(user_id);
CREATE INDEX IF NOT EXISTS idx_login_log_login_time ON t_login_log(login_time);

-- 操作日志表
CREATE TABLE IF NOT EXISTS t_operation_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    username TEXT NOT NULL,
    module TEXT,
    operation_type TEXT,
    description TEXT,
    method TEXT,
    params TEXT,
    result TEXT,
    ip_address TEXT,
    location TEXT,
    status INTEGER NOT NULL DEFAULT 1,
    error_msg TEXT,
    operation_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 操作日志索引
CREATE INDEX IF NOT EXISTS idx_operation_log_username ON t_operation_log(username);
CREATE INDEX IF NOT EXISTS idx_operation_log_user_id ON t_operation_log(user_id);
CREATE INDEX IF NOT EXISTS idx_operation_log_module ON t_operation_log(module);
CREATE INDEX IF NOT EXISTS idx_operation_log_operation_time ON t_operation_log(operation_time);

-- 插件信息表
CREATE TABLE IF NOT EXISTS t_plugin (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    plugin_key TEXT NOT NULL UNIQUE,
    category TEXT NOT NULL,
    description TEXT,
    icon TEXT,
    versions TEXT,
    default_version TEXT,
    install_script TEXT,
    uninstall_script TEXT,
    check_script TEXT,
    sort INTEGER DEFAULT 0,
    status TEXT DEFAULT 'ENABLED',
    create_time TEXT DEFAULT CURRENT_TIMESTAMP,
    update_time TEXT DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 插件安装记录表
CREATE TABLE IF NOT EXISTS t_plugin_install (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    server_id INTEGER NOT NULL,
    plugin_id INTEGER NOT NULL,
    version TEXT NOT NULL,
    status TEXT DEFAULT 'INSTALLING',
    log TEXT,
    install_path TEXT,
    install_time TEXT,
    operate_by TEXT,
    create_time TEXT DEFAULT CURRENT_TIMESTAMP,
    update_time TEXT DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 插件安装索引
CREATE INDEX IF NOT EXISTS idx_plugin_install_server ON t_plugin_install(server_id);
CREATE INDEX IF NOT EXISTS idx_plugin_install_plugin ON t_plugin_install(plugin_id);

-- 项目成员表
CREATE TABLE IF NOT EXISTS t_project_member (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    project_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    role_type TEXT DEFAULT 'MEMBER',
    create_time TEXT DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(project_id, user_id),
    FOREIGN KEY (project_id) REFERENCES t_project(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
);

-- 项目服务器关联表
CREATE TABLE IF NOT EXISTS t_project_server (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    project_id INTEGER NOT NULL,
    server_id INTEGER NOT NULL,
    create_time TEXT DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(project_id, server_id),
    FOREIGN KEY (project_id) REFERENCES t_project(id) ON DELETE CASCADE,
    FOREIGN KEY (server_id) REFERENCES t_server(id) ON DELETE CASCADE
);
