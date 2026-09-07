-- Docker Compose 插件种子数据；可重复执行
INSERT INTO t_plugin (name, plugin_key, category, description, icon, versions, default_version,
  install_script, uninstall_script, check_script, sort, status, deleted)
SELECT 'Docker Compose', 'docker-compose', 'BASE_ENV', CONVERT(0x446f636b657220436f6d706f736520e7bc96e68e92e5b7a5e585b7 USING utf8mb4), 'LogoDocker',
  '["v2", "latest"]', 'latest',
  '#!/bin/bash\nset -e\ncommand -v docker >/dev/null 2>&1 || { echo "请先安装 Docker"; exit 1; }\nif ! docker compose version >/dev/null 2>&1; then yum install -y docker-compose-plugin; fi\ndocker compose version',
  '#!/bin/bash\necho "Docker Compose 为 Docker 插件，保留 Docker 主程序，不执行自动卸载"',
  'docker compose version', 9, 'ENABLED', 0
WHERE NOT EXISTS (SELECT 1 FROM t_plugin WHERE plugin_key = 'docker-compose');
