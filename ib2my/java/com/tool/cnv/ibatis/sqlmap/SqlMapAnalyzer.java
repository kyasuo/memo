package com.tool.cnv.ibatis.sqlmap;

import java.io.File;
import java.util.List;

import com.tool.cnv.ibatis.sqlmap.bean.SqlMapInfo;

public interface SqlMapAnalyzer {

	List<SqlMapInfo> analyze(File xml);

}
