#!/usr/bin/env bash
set -euo pipefail

APP_DIR=/opt/certpath
MYSQL_HOME=/www/server/mysql
DATA_DIR="$APP_DIR/mysql-data"
RUNTIME_DIR="$APP_DIR/mysql-runtime"
SOCKET="$RUNTIME_DIR/mysql.sock"

install -d -m 750 -o mysql -g mysql "$DATA_DIR" "$RUNTIME_DIR"

cat > "$APP_DIR/mysql.cnf" <<'EOF'
[mysqld]
basedir=/www/server/mysql
datadir=/opt/certpath/mysql-data
socket=/opt/certpath/mysql-runtime/mysql.sock
pid-file=/opt/certpath/mysql-runtime/mysql.pid
log-error=/opt/certpath/mysql-runtime/mysql-error.log
port=13306
bind-address=127.0.0.1
user=mysql
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
skip-name-resolve=1
max_connections=100
innodb_buffer_pool_size=256M

[client]
socket=/opt/certpath/mysql-runtime/mysql.sock
port=13306
EOF
chmod 640 "$APP_DIR/mysql.cnf"
chown root:mysql "$APP_DIR/mysql.cnf"

if [[ ! -f "$DATA_DIR/auto.cnf" ]]; then
  "$MYSQL_HOME/bin/mysqld" --defaults-file="$APP_DIR/mysql.cnf" --initialize-insecure --user=mysql
fi

cat > /etc/systemd/system/certpath-mysql.service <<'EOF'
[Unit]
Description=CertPath isolated MySQL
After=network.target

[Service]
Type=simple
User=mysql
Group=mysql
ExecStart=/www/server/mysql/bin/mysqld --defaults-file=/opt/certpath/mysql.cnf
Restart=on-failure
RestartSec=5
TimeoutStopSec=120
LimitNOFILE=65535

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable --now certpath-mysql.service

for _ in {1..30}; do
  if "$MYSQL_HOME/bin/mysqladmin" --protocol=socket --socket="$SOCKET" -uroot ping >/dev/null 2>&1; then
    break
  fi
  sleep 1
done
"$MYSQL_HOME/bin/mysqladmin" --protocol=socket --socket="$SOCKET" -uroot ping >/dev/null

set -a
source "$APP_DIR/.env"
set +a

"$MYSQL_HOME/bin/mysql" --protocol=socket --socket="$SOCKET" -uroot <<SQL
CREATE DATABASE IF NOT EXISTS \`$MYSQL_DATABASE\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '$MYSQL_USER'@'127.0.0.1' IDENTIFIED BY '$MYSQL_PASSWORD';
CREATE USER IF NOT EXISTS '$MYSQL_USER'@'localhost' IDENTIFIED BY '$MYSQL_PASSWORD';
GRANT ALL PRIVILEGES ON \`$MYSQL_DATABASE\`.* TO '$MYSQL_USER'@'127.0.0.1';
GRANT ALL PRIVILEGES ON \`$MYSQL_DATABASE\`.* TO '$MYSQL_USER'@'localhost';
FLUSH PRIVILEGES;
SQL

"$MYSQL_HOME/bin/mysql" --protocol=socket --socket="$SOCKET" -uroot -Nse \
  "SELECT VERSION(), @@port, @@bind_address; SHOW DATABASES LIKE 'certificate_management';"
systemctl is-active certpath-mysql.service
