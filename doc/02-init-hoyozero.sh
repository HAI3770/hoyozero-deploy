#!/bin/sh
set -eu

admin_name="${HOYOZERO_INITIAL_ADMIN_USERNAME:-admin}"
admin_password="${HOYOZERO_INITIAL_ADMIN_PASSWORD:?HOYOZERO_INITIAL_ADMIN_PASSWORD is required}"

case "$admin_name" in
  ''|*[!a-zA-Z0-9_-]*)
    echo "HOYOZERO_INITIAL_ADMIN_USERNAME may contain only letters, numbers, _ and -" >&2
    exit 1
    ;;
esac

admin_hash="$(printf %s "$admin_password" | md5sum | awk '{print $1}')"

mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" <<SQL
INSERT INTO t_user (id, username, password, nickname, status)
VALUES (1, '${admin_name}', '${admin_hash}', '平台管理员', 1);
INSERT INTO t_user_role (id, user_id, role_id) VALUES (1, 1, 1);
SQL
