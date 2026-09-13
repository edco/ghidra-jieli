#!/usr/bin/env bash
#
# build_gdt.sh - Compile C peripheral headers into Ghidra Data Type Archives (.gdt)
#                without launching the Ghidra GUI or analyzeHeadless.
#
# Usage (run from inside the ghidra-jieli directory):
#   export GHIDRA_HOME=/path/to/ghidra
#   ./tools/build_gdt.sh
#

set -e

if [[ -z "${GHIDRA_HOME}" || ! -d "${GHIDRA_HOME}" ]]; then
    echo "Error: GHIDRA_HOME environment variable is not set or directory does not exist."
    echo "Please set GHIDRA_HOME to your Ghidra installation directory (e.g. export GHIDRA_HOME=/opt/ghidra)."
    exit 1
fi

HEADER_FILE="data/headers/ac7911b8_peripherals.h"
GDT_FILE="data/typeinfo/ac7911b8_peripherals.gdt"
JAVA_SRC="tools/BuildGdtCli.java"

if [[ ! -f "${HEADER_FILE}" ]]; then
    echo "Error: Cannot find ${HEADER_FILE}."
    echo "Please run this script from inside the ghidra-jieli root directory: ./tools/build_gdt.sh"
    exit 1
fi

if [[ ! -f "${JAVA_SRC}" ]]; then
    echo "Error: Cannot find ${JAVA_SRC}."
    exit 1
fi

echo "[1/2] Building Ghidra classpath..."
CLASSPATH=$(find "${GHIDRA_HOME}/Ghidra/Framework" "${GHIDRA_HOME}/Ghidra/Features" -name "*.jar" 2>/dev/null | tr '\n' ':')

BUILD_DIR="build/gdt_build"
mkdir -p "${BUILD_DIR}"

echo "[2/2] Compiling ${GDT_FILE}..."
javac -cp "${CLASSPATH}" -d "${BUILD_DIR}" "${JAVA_SRC}"
java -cp "${BUILD_DIR}:${CLASSPATH}" BuildGdtCli "${HEADER_FILE}" "${GDT_FILE}"

rm -rf "${BUILD_DIR}"
