#!/usr/bin/env bash
set -euo pipefail

DOMAIN="${DOMAIN:-test.falshcode.xyz}"
APP_DIR="${APP_DIR:-/opt/certpath}"
WEBROOT="/www/wwwroot/$DOMAIN"
VHOST="/www/server/panel/vhost/nginx/$DOMAIN.conf"
CERT_DIR="/etc/letsencrypt/live/$DOMAIN"

if [[ $EUID -ne 0 ]]; then
  echo "This script must run as root." >&2
  exit 1
fi

command -v nginx >/dev/null
command -v certbot >/dev/null
test -f "$APP_DIR/.env"

install -d -m 755 "$WEBROOT/.well-known/acme-challenge"

if grep -q '^CORS_ALLOWED_ORIGINS=' "$APP_DIR/.env"; then
  sed -i "s#^CORS_ALLOWED_ORIGINS=.*#CORS_ALLOWED_ORIGINS=https://$DOMAIN#" "$APP_DIR/.env"
else
  printf '\nCORS_ALLOWED_ORIGINS=https://%s\n' "$DOMAIN" >> "$APP_DIR/.env"
fi

if [[ ! -s "$CERT_DIR/fullchain.pem" || ! -s "$CERT_DIR/privkey.pem" ]]; then
  cat > "$VHOST" <<EOF
server {
    listen 80;
    server_name $DOMAIN;

    location ^~ /.well-known/acme-challenge/ {
        root $WEBROOT;
        default_type text/plain;
    }

    location ~ /\.(?!well-known).* {
        return 404;
    }

    location / {
        proxy_pass http://127.0.0.1:6060;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

  nginx -t
  nginx -s reload

  certbot certonly \
    --webroot \
    --webroot-path "$WEBROOT" \
    --domain "$DOMAIN" \
    --preferred-challenges http \
    --non-interactive \
    --agree-tos \
    --register-unsafely-without-email
fi

cat > "$VHOST" <<EOF
server {
    listen 80;
    server_name $DOMAIN;

    location ^~ /.well-known/acme-challenge/ {
        root $WEBROOT;
        default_type text/plain;
    }

    location / {
        return 301 https://\$host\$request_uri;
    }
}

server {
    listen 443 ssl;
    http2 on;
    server_name $DOMAIN;
    server_tokens off;

    ssl_certificate $CERT_DIR/fullchain.pem;
    ssl_certificate_key $CERT_DIR/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_session_cache shared:CertPathSSL:10m;
    ssl_session_timeout 1d;
    ssl_session_tickets off;

    client_max_body_size 12m;

    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "DENY" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    add_header Permissions-Policy "camera=(), microphone=(), geolocation=()" always;
    add_header Content-Security-Policy "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; font-src 'self' data: https://fonts.gstatic.com; img-src 'self' data: blob:; connect-src 'self'; object-src 'none'; base-uri 'self'; frame-ancestors 'none'; form-action 'self'; upgrade-insecure-requests" always;

    location ~ /\.(?!well-known).* {
        return 404;
    }

    location ^~ /api/ {
        proxy_pass http://127.0.0.1:18081;
        proxy_http_version 1.1;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
        proxy_read_timeout 60s;
        proxy_send_timeout 60s;
    }

    location / {
        proxy_pass http://127.0.0.1:6060;
        proxy_http_version 1.1;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
        proxy_read_timeout 60s;
        proxy_send_timeout 60s;
    }
}
EOF

install -d -m 755 /etc/letsencrypt/renewal-hooks/deploy
cat > /etc/letsencrypt/renewal-hooks/deploy/reload-nginx.sh <<'EOF'
#!/usr/bin/env bash
set -e
nginx -t
nginx -s reload
EOF
chmod 755 /etc/letsencrypt/renewal-hooks/deploy/reload-nginx.sh

if systemctl cat certbot-renew.timer >/dev/null 2>&1; then
  systemctl enable --now certbot-renew.timer >/dev/null
fi

nginx -t
nginx -s reload

echo "HTTPS configured for https://$DOMAIN"
