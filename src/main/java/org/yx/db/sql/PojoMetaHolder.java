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
package org.yx.db.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.yx.annotation.Exclude;
import org.yx.annotation.db.Column;
import org.yx.annotation.db.Table;
import org.yx.exception.SumkException;
import org.yx.log.Logs;
import org.yx.util.S;

public class PojoMetaHolder {

	private static final ConcurrentMap<Class<?>, PojoMeta> pojoMetas = new ConcurrentHashMap<>();
	private static final ConcurrentMap<String, PojoMeta> tableMetas = new ConcurrentHashMap<>();

	public static PojoMeta getTableMeta(String table) {
		return tableMetas.get(table);
	}

	public static List<PojoMeta> allPojoMeta() {
		return new ArrayList<>(pojoMetas.values());
	}

	public static PojoMeta getPojoMeta(PojoMeta pm, String sub) {
		PojoMeta temp = tableMetas.get(pm.subTableName(sub));
		if (temp != null) {
			return temp;
		}
		pm = pm.subPojoMeta(sub);
		temp = tableMetas.putIfAbsent(pm.getTableName(), pm);
		return temp != null ? temp : pm;
	}

	public static PojoMeta getPojoMeta(Class<?> clz, String sub) {
		PojoMeta pm = getPojoMeta(clz);
		if (pm == null) {
			return null;
		}
		if (sub == null || sub.isEmpty()) {
			return pm;
		}
		PojoMeta temp = tableMetas.get(pm.subTableName(sub));
		if (temp != null) {
			return temp;
		}
		pm = pm.subPojoMeta(sub);
		temp = tableMetas.putIfAbsent(pm.getTableName(), pm);
		return temp != null ? temp : pm;
	}

	public static PojoMeta getPojoMeta(Class<?> clz) {
		if (clz == null || clz.isInterface() || clz == Object.class) {
			return null;
		}
		Class<?> tmp = clz;
		while (tmp != Object.class) {
			PojoMeta m = pojoMetas.get(tmp);
			if (m != null) {
				return m;
			}
			tmp = tmp.getSuperclass();
		}
		return null;
	}

	public static void resolve(Class<?> pojoClz) {
		Table table = pojoClz.getAnnotation(Table.class);
		if (table == null) {
			return;
		}

		Field[] fs = S.bean().getFields(pojoClz);
		Map<String, Field> map = new LinkedHashMap<>();
		for (Field f : fs) {
			if (f.isAnnotationPresent(Exclude.class)) {
				continue;
			}
			map.putIfAbsent(f.getName(), f);
		}
		Collection<Field> set = map.values();
		List<ColumnMeta> list = new ArrayList<>(set.size());
		for (Field f : set) {
			Column c = f.getAnnotation(Column.class);
			f.setAccessible(true);
			list.add(new ColumnMeta(f, c));
		}
		if (list.isEmpty()) {
			Logs.db().debug("{}'s column is empty", pojoClz.getName());
			return;
		}
		Collections.sort(list);
		PojoMeta tm = new PojoMeta(table, list.toArray(new ColumnMeta[list.size()]), pojoClz);
		if (tm.databaseIds.isEmpty()) {
			throw new SumkException(56456456, pojoClz.getName() + " has no database primary key");
		}
		pojoMetas.put(pojoClz, tm);
		tableMetas.put(tm.getTableName(), tm);
	}
}
