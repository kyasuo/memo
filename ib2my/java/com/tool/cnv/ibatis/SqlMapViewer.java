package com.tool.cnv.ibatis;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tool.cnv.ibatis.sqlmap.SqlMapAnalyzer;
import com.tool.cnv.ibatis.sqlmap.SqlMapAnalyzerImpl;

public class SqlMapViewer {

	static final Logger logger = LoggerFactory.getLogger(SqlMapViewer.class);
	static final ObjectMapper MAPPER = new ObjectMapper();
	static final SqlMapAnalyzer ANALYZER = new SqlMapAnalyzerImpl();

	public static void main(String[] args) throws Exception {
		logger.info("start");
		int numOfFiles = 0;
		for (String arg : args) {
			final File dir = new File(arg);
			if (!dir.exists() || !dir.isDirectory()) {
				continue;
			}
			for (File file : FileUtils.listFiles(dir, new String[] { "xml" }, true)) {
				if (!file.getName().endsWith("_sqlMap.xml")) {
					continue;
				}
				logger.info(
						"SqlMap file = " + file.getPath() + "\r\n" + MAPPER.writeValueAsString(ANALYZER.analyze(file)));
				numOfFiles++;
			}
		}
		logger.info("end. The number of processing files is " + numOfFiles);
	}

}
