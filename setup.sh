#!/usr/bin/env bash
#
# setup.sh — Script de setup do Sistema de Ponto Eletrônico
#
# Baixa o driver PostgreSQL JDBC e compila o projeto.
# Uso: bash setup.sh
#

set -e

# Garantir que este script seja executado a partir do seu próprio diretório
cd "$(dirname "$0")"

DRIVER_VERSION="42.7.3"
DRIVER_URL="https://jdbc.postgresql.org/download/postgresql-${DRIVER_VERSION}.jar"
LIB_DIR="lib"
OUT_DIR="out"
SRC_DIR="src"

echo "================================================"
echo "  Setup — Sistema de Ponto Eletrônico"
echo "================================================"

# ---------------------------------------------------------------------------
# 1. Garantir que os diretórios existam
# ---------------------------------------------------------------------------
mkdir -p "$LIB_DIR" "$OUT_DIR"

# ---------------------------------------------------------------------------
# 2. Baixar o driver PostgreSQL JDBC (se não existir)
# ---------------------------------------------------------------------------
DRIVER_JAR="${LIB_DIR}/postgresql-${DRIVER_VERSION}.jar"

if [ -f "$DRIVER_JAR" ]; then
    echo "[✓] Driver já existe: ${DRIVER_JAR}"
else
    echo "[~] Baixando driver PostgreSQL JDBC ${DRIVER_VERSION}..."
    if command -v wget &>/dev/null; then
        wget -q "$DRIVER_URL" -O "$DRIVER_JAR"
    elif command -v curl &>/dev/null; then
        curl -sL "$DRIVER_URL" -o "$DRIVER_JAR"
    else
        echo "[✗] Erro: wget ou curl não encontrado. Instale um dos dois e tente novamente."
        exit 1
    fi
    echo "[✓] Driver baixado: ${DRIVER_JAR}"
fi

# ---------------------------------------------------------------------------
# 3. Compilar o projeto
# ---------------------------------------------------------------------------
echo "[~] Compilando o projeto..."
CP="${DRIVER_JAR}"

# Coletar todos os arquivos .java recursivamente
JAVA_FILES=$(find "$SRC_DIR" -name '*.java' 2>/dev/null || true)

if [ -z "$JAVA_FILES" ]; then
    echo "[!] Nenhum arquivo Java encontrado em ${SRC_DIR}/. Pulando compilação."
else
    javac -cp "$CP" -d "$OUT_DIR" $JAVA_FILES
    echo "[✓] Compilação concluída."
fi

# ---------------------------------------------------------------------------
# 4. Sumário
# ---------------------------------------------------------------------------
echo ""
echo "================================================"
echo "  Setup concluído com sucesso!"
echo "================================================"
echo ""
echo "  Para executar o sistema:"
echo "    java -cp ${OUT_DIR}:${DRIVER_JAR} br.com.ponto.Main"
echo ""
echo "  Scripts SQL disponíveis em: sql/"
echo "    psql -U paulo -d ponto_eletronico -f sql/01_ddl.sql"
echo "    psql -U paulo -d ponto_eletronico -f sql/02_dml.sql"
echo ""
