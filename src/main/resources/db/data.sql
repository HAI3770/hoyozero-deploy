-- SQLite 初始数据
-- 基于 MySQL 脚本转换

-- 初始用户数据 (密码: 123456 的 MD5 加密)
INSERT OR IGNORE INTO t_user (id, username, password, nickname, email, status) VALUES 
(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 'admin@hoyozero.com', 1);

-- 角色数据
INSERT OR IGNORE INTO t_role (id, name, code, description) VALUES 
(1, '系统管理员', 'ADMIN', '拥有系统所有权限'),
(2, '项目管理员', 'PROJECT_ADMIN', '管理所有项目'),
(3, '开发者', 'DEVELOPER', '可以查看和触发构建'),
(4, '普通用户', 'USER', '只能查看分配给自己的项目');

-- 用户角色关联
INSERT OR IGNORE INTO t_user_role (id, user_id, role_id) VALUES 
(1, 1, 1);

-- 菜单数据
INSERT OR IGNORE INTO t_menu (id, parent_id, name, path, icon, sort_order, menu_type, visible) VALUES 
(1, 0, '仪表盘', '/dashboard', 'SpeedometerSharp', 1, 'MENU', 1),
(2, 0, '项目管理', '/project', 'FolderOpenSharp', 2, 'MENU', 1),
(3, 0, '服务器管理', '/server', 'ServerSharp', 4, 'MENU', 1),
(4, 0, '构建历史', '/build', 'RocketSharp', 3, 'MENU', 1),
(5, 0, '系统管理', 'system', 'ShieldCheckmarkSharp', 5, 'MENU', 1),
(6, 5, '用户管理', '/user', 'PeopleSharp', 1, 'MENU', 1),
(7, 5, '角色管理', '/role', 'ShieldCheckmarkSharp', 2, 'MENU', 1),
(8, 5, '菜单管理', '/menu', 'MenuSharp', 3, 'MENU', 1),
(9, 0, '插件市场', '/plugin', 'ExtensionPuzzleSharp', 10, 'MENU', 1),
(10, 0, '文件管理', '/file', 'FolderOpenSharp', 5, 'MENU', 1),
(11, 0, '日志管理', '/log', 'DocumentTextSharp', 6, 'MENU', 1),
(12, 11, '登录日志', '/log/login', 'LogInSharp', 1, 'MENU', 1),
(13, 11, '操作日志', '/log/operation', 'ListSharp', 0, 'MENU', 1),
(14, 0, '项目组管理', '/project-group', 'GitNetworkSharp', 2, 'MENU', 1);

-- 角色菜单关联 - 管理员拥有所有菜单
INSERT OR IGNORE INTO t_role_menu (id, role_id, menu_id) VALUES 
(1, 1, 1), (2, 1, 2), (3, 1, 3), (4, 1, 4), (5, 1, 5), (6, 1, 6),
(7, 1, 7), (8, 1, 8), (9, 1, 9), (10, 1, 10), (11, 1, 11), (12, 1, 12), (13, 1, 13), (14, 1, 14);

-- 角色菜单关联 - 项目管理员
INSERT OR IGNORE INTO t_role_menu (id, role_id, menu_id) VALUES 
(15, 2, 1), (16, 2, 2), (17, 2, 3), (18, 2, 4), (19, 2, 14);

-- 角色菜单关联 - 开发者
INSERT OR IGNORE INTO t_role_menu (id, role_id, menu_id) VALUES 
(20, 3, 1), (21, 3, 2), (22, 3, 3), (23, 3, 4), (24, 3, 9), (25, 3, 10), (26, 3, 11), (27, 3, 12), (28, 3, 13), (29, 3, 14);

-- 角色菜单关联 - 普通用户
INSERT OR IGNORE INTO t_role_menu (id, role_id, menu_id) VALUES 
(30, 4, 1), (31, 4, 2), (32, 4, 14);

-- 插件数据
INSERT OR IGNORE INTO t_plugin (id, name, plugin_key, category, description, icon, versions, default_version, install_script, uninstall_script, sort, status) VALUES 
(1, 'Git', 'git', 'BASE_ENV', '分布式版本控制系统', 'GitBranchSharp', '["2.40", "2.41", "latest"]', 'latest', 'yum install -y git && git --version', 'yum remove -y git', 1, 'ENABLED'),
(2, 'JDK 8', 'jdk8', 'BASE_ENV', 'Java Development Kit 8', 'CodeSharp', '["8u391", "8u392", "8u401"]', '8u401', 'yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel && java -version', 'yum remove -y java-1.8.0-openjdk java-1.8.0-openjdk-devel', 2, 'ENABLED'),
(3, 'JDK 17', 'jdk17', 'BASE_ENV', 'Java Development Kit 17 LTS', 'CodeSharp', '["17.0.9", "17.0.10", "17.0.11"]', '17.0.11', 'yum install -y java-17-openjdk java-17-openjdk-devel && java -version', 'yum remove -y java-17-openjdk java-17-openjdk-devel', 3, 'ENABLED'),
(4, 'Node.js LTS', 'nodejs', 'BASE_ENV', 'JavaScript 运行时环境', 'LogoNodejs', '["18.19.0", "20.11.0", "latest"]', '20.11.0', 'curl -fsSL https://rpm.nodesource.com/setup_20.x | bash - && yum install -y nodejs && node --version && npm --version', 'yum remove -y nodejs', 4, 'ENABLED'),
(5, 'Docker', 'docker', 'BASE_ENV', '容器化平台', 'LogoDocker', '["24.0", "25.0", "latest"]', 'latest', 'curl -fsSL https://get.docker.com | bash && systemctl start docker && systemctl enable docker && docker --version', 'systemctl stop docker && yum remove -y docker-ce docker-ce-cli containerd.io && rm -rf /var/lib/docker', 5, 'ENABLED'),
(6, 'Python 3', 'python3', 'BASE_ENV', 'Python 编程语言', 'LogoPython', '["3.10", "3.11", "3.12"]', '3.11', 'yum install -y python3 python3-pip && python3 --version && pip3 --version', 'yum remove -y python3 python3-pip', 6, 'ENABLED'),
(7, 'Maven', 'maven', 'BASE_ENV', 'Java 项目管理工具', 'ConstructSharp', '["3.9.11", "latest"]', '3.9.11', 'cd /tmp && wget https://dlcdn.apache.org/maven/maven-3/3.9.11/binaries/apache-maven-3.9.11-bin.tar.gz && tar -zxf apache-maven-3.9.11-bin.tar.gz -C /usr/local/ && ln -sf /usr/local/apache-maven-3.9.11/bin/mvn /usr/bin/mvn && mvn --version', 'rm -rf /usr/local/apache-maven-* && rm -f /usr/bin/mvn', 7, 'ENABLED'),
(8, 'Nginx', 'nginx', 'MIDDLEWARE', '高性能 Web 服务器', 'ServerSharp', '["1.24", "1.25", "latest"]', 'latest', 'yum install -y nginx && systemctl start nginx && systemctl enable nginx && nginx -v', 'systemctl stop nginx && yum remove -y nginx', 1, 'ENABLED'),
(9, 'MySQL 8', 'mysql', 'MIDDLEWARE', '关系型数据库', 'ServerSharp', '["8.0.35", "8.0.36", "latest"]', '8.0.36', 'yum install -y mysql-server && systemctl start mysqld && systemctl enable mysqld && mysql --version', 'systemctl stop mysqld && yum remove -y mysql-server && rm -rf /var/lib/mysql', 2, 'ENABLED'),
(10, 'Redis', 'redis', 'MIDDLEWARE', '内存数据库', 'ServerSharp', '["7.0", "7.2", "latest"]', '7.2', 'yum install -y redis && systemctl start redis && systemctl enable redis && redis-server --version', 'systemctl stop redis && yum remove -y redis', 3, 'ENABLED'),
(11, 'MongoDB', 'mongodb', 'MIDDLEWARE', 'NoSQL 文档数据库', 'ServerSharp', '["6.0", "7.0", "latest"]', '7.0', 'yum install -y mongodb-org && systemctl start mongod && systemctl enable mongod && mongod --version', 'systemctl stop mongod && yum remove -y mongodb-org && rm -rf /var/lib/mongo', 4, 'ENABLED');
