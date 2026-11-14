#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
CATALINA_HOME="${CATALINA_HOME:-/opt/tomcat}"

export DB_URL="${DB_URL:-jdbc:mysql://127.0.0.1:3306/meridian_bank?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8&serverTimezone=UTC&connectionTimeZone=UTC}"
export DB_USER="${DB_USER:-bankuser}"
export DB_PASSWORD="${DB_PASSWORD:-meridian_local}"
export JAVA_OPTS="${JAVA_OPTS:--Xms128m -Xmx384m}"
export CATALINA_PID="${CATALINA_HOME}/catalina.pid"

if [[ ! -x "${CATALINA_HOME}/bin/startup.sh" ]]; then
  echo "Tomcat was not found at ${CATALINA_HOME}." >&2
  exit 1
fi

mvn -f "${ROOT}/pom.xml" -B -DskipTests package

if [[ -f "${CATALINA_PID}" ]] || [[ -n "$(pgrep -f "${CATALINA_HOME}" || true)" ]]; then
  "${CATALINA_HOME}/bin/shutdown.sh" || true
  sleep 3
fi

rm -rf "${CATALINA_HOME}/webapps/ROOT" "${CATALINA_HOME}/webapps/ROOT.war"
cp "${ROOT}/target/meridian-bank.war" "${CATALINA_HOME}/webapps/ROOT.war"
"${CATALINA_HOME}/bin/startup.sh"
echo "Meridian Bank is starting on http://127.0.0.1:8844/"
