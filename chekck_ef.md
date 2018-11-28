
## check empty file

```
MSG_PREFIX="all > [Caution] The branch has empty files. (REPOSITORY: ${GIT_URL}, BRANCH: ${GIT_BRANCH})"
MSG_SUFFIX="------------------------------------------------------------------------------------------------------------------------"
TMP_FILE="EMPTY_LIST.txt"

EMPTY_LIST=`find . -empty -type f |grep -v ${TMP_FILE} |grep -v ".gitkeep" |sed "s/^\.\// - /" > ${TMP_FILE}`
RESULT=`cat ${TMP_FILE} |wc -l`

if [ ${RESULT} -ge 1 ]; then
  (
    echo "NICK bot"
    echo "USER bot 8 * : bot"
    sleep 1
    echo "JOIN #talk"
    echo "PRIVMSG #talk : ${MSG_PREFIX}"
    cat ${TMP_FILE} | while read line
    do
      echo "PRIVMSG #talk : ${line}"
    done
    echo "PRIVMSG #talk : ${MSG_SUFFIX}"
    echo QUIT
  ) | nc localhost 6667
fi
```
