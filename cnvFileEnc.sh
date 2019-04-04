#!/bin/sh

INPUT_BASE=input
OUTPUT_DIR=output
ENC_FROM=SHIFT_JIS
ENC_TO=UTF-8
SCRIPT=command.sh

echo "#!/bin/sh" > ${SCRIPT}
find ${INPUT_BASE} -type d |awk -v prefix="mkdir -p ${OUTPUT_DIR}/" '{print prefix$1 }' >> ${SCRIPT}
find ${INPUT_BASE} -type f |awk -v prefix="iconv -f ${ENC_FROM} -t ${ENC_TO} -s " -v suffix=" > ${OUTPUT_DIR}/" '{print prefix$1suffix$1}' >> ${SCRIPT}
