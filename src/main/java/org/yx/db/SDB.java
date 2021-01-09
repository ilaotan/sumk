/**
 * Copyright (C) 2016 - 2030 youtongluan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.yx.db;

import java.util.List;
import java.util.Map;

import org.yx.db.kit.DBKits;
import org.yx.db.kit.SDBuilder;
import org.yx.db.mapper.NamedExecutor;
import org.yx.db.mapper.SqlHolder;
import org.yx.db.sql.InsertResult;

public class SDB {

	public static int execute(String name, Map<String, Object> map) {
		return NamedExecutor.execute(SqlHolder.findSql(name), map);
	}

	public static InsertResult insertWithAutoGeneratedKeys(String name, Map<String, Object> map) {
		return NamedExecutor.insertWithAutoGeneratedKeys(SqlHolder.findSql(name), map);
	}

	public static List<Map<String, Object>> list(String name, Map<String, Object> map) {
		return NamedExecutor.list(SqlHolder.findSql(name), map);
	}

	public static List<Object[]> listInArray(String name, Map<String, Object> map) {
		return NamedExecutor.listInArray(SqlHolder.findSql(name), map);
	}

	public static List<?> singleColumnList(String name, Map<String, Object> map) {
		return NamedExecutor.singleColumnList(SqlHolder.findSql(name), map);
	}

	public static long count(String name, Map<String, Object> map) {
		return NamedExecutor.count(SqlHolder.findSql(name), map);
	}

	public static Map<String, Object> queryOne(String name, Map<String, Object> map) {
		return DBKits.queryOne(list(name, map));
	}

	public static SDBuilder builder() {
		return new SDBuilder();
	}

	public static SDBuilder builder(String name, Object param) {
		return new SDBuilder().name(name).param(param);
	}
}