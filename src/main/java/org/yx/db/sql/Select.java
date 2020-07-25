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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.yx.db.event.DBEventPublisher;
import org.yx.db.event.QueryEvent;
import org.yx.db.visit.Exchange;
import org.yx.db.visit.PojoResultHandler;
import org.yx.db.visit.ResultHandler;
import org.yx.db.visit.SumkDbVisitor;
import org.yx.exception.SumkException;
import org.yx.util.Asserts;
import org.yx.util.CollectionUtil;

/**
 * 比较跟整个addEqual是add关系。同一种比较类型，比如less，它的一个key只能设置一次，后设置的会覆盖前面设置的<BR>
 * 比较中用到的key，都是java中的key，大小写敏感.
 */
public class Select extends SelectBuilder {
	public Select(SumkDbVisitor<List<Map<String, Object>>> visitor) {
		super(visitor);
	}

	/**
	 * @param fail
	 *            如果为true，会验证map参数中，是否存在无效的key，预防开发人员将key写错。默认为true
	 * @return 当前对象
	 */
	public Select failIfPropertyNotMapped(boolean fail) {
		this.failIfPropertyNotMapped = fail;
		return this;
	}

	/**
	 * 物理分表的情况下，设置分区名。这个方法只能调用一次
	 * 
	 * @param sub
	 *            分区名
	 * @return 当前对象
	 */
	public Select partition(String sub) {
		sub(sub);
		return this;
	}

	/**
	 * 允许不设置where条件
	 * 
	 * @param empty
	 *            true表示允许where条件为空
	 * @return 当前对象
	 */
	public Select allowEmptyWhere(boolean empty) {
		this.allowEmptyWhere = empty;
		return this;
	}

	private ResultHandler resultHandler;

	public Select resultHandler(ResultHandler resultHandler) {
		this.resultHandler = Objects.requireNonNull(resultHandler);
		return this;
	}

	public Select compareNullPolicy(CompareNullPolicy policy) {
		this.compareNullPolicy = Objects.requireNonNull(policy);
		return this;
	}

	@SuppressWarnings("unchecked")
	private Select setCompare(int index, Map<String, Object> map) {
		if (CollectionUtil.isEmpty(map)) {
			return this;
		}
		if (_compare == null) {
			_compare = new HashMap[COMPARES.length];
		}
		this._compare[index] = map;
		return this;
	}

	@SuppressWarnings("unchecked")
	private Select setCompare(int index, String key, Object value) {
		if (key == null || key.isEmpty()) {
			return this;
		}
		if (_compare == null) {
			_compare = new HashMap[COMPARES.length];
		}
		if (this._compare[index] == null) {
			this._compare[index] = new HashMap<>();
		}
		this._compare[index].put(key, value);
		return this;
	}

	/**
	 * 设置大于,一个key只能设置一次，后设置的会覆盖前面设置的。<BR>
	 * 
	 * @param key
	 *            java字段的名称
	 * @param value
	 *            值
	 * @return 当前对象
	 */
	public Select bigThan(String key, Object value) {
		return setCompare(BIG, key, value);
	}

	public Select bigOrEqual(String key, Object value) {
		return setCompare(BIG_EQUAL, key, value);
	}

	public Select lessThan(String key, Object value) {
		return setCompare(LESS, key, value);
	}

	public Select lessOrEqual(String key, Object value) {
		return setCompare(LESS_EQUAL, key, value);
	}

	/**
	 * like操作，%号要自己添加
	 * 
	 * @param key
	 *            字段名
	 * @param value
	 *            值，不会自动添加%
	 * @return 当前对象
	 */
	public Select like(String key, Object value) {
		return setCompare(LIKE, key, value);
	}

	/**
	 * 不等于操作
	 * 
	 * @param key
	 *            字段名
	 * @param value
	 *            值，不会自动添加%
	 * @return 当前对象
	 */
	public Select not(String key, Object value) {
		return setCompare(NOT, key, value);
	}

	public Select bigThan(Map<String, Object> map) {
		return setCompare(BIG, map);
	}

	/**
	 * 大于等于
	 * 
	 * @param map
	 *            对map中所有的kv做大于等于操作
	 * @return 当前对象
	 */
	public Select bigOrEqual(Map<String, Object> map) {
		return setCompare(BIG_EQUAL, map);
	}

	public Select lessThan(Map<String, Object> map) {
		return setCompare(LESS, map);
	}

	/**
	 * 小于或等于
	 * 
	 * @param map
	 *            对map中所有的kv做小于等于操作
	 * @return 当前对象
	 */
	public Select lessOrEqual(Map<String, Object> map) {
		return setCompare(LESS_EQUAL, map);
	}

	public Select like(Map<String, Object> map) {
		return setCompare(LIKE, map);
	}

	public Select not(Map<String, Object> map) {
		return setCompare(NOT, map);
	}

	/**
	 * 升序排列。asc和desc的调用顺序决定了在sql中出现的顺序。 此方法可以调用多次
	 * 
	 * @param field
	 *            升序字段
	 * @return 当前对象
	 */
	public Select orderByAsc(String field) {
		return this.addOrderBy(field, false);
	}

	private Select addOrderBy(String name, boolean desc) {
		if (this.orderby == null) {
			this.orderby = new ArrayList<>(2);
		}
		this.orderby.add(new Order(name, desc));
		return this;
	}

	/**
	 * 增加降序排列
	 * 
	 * @param field
	 *            降序字段
	 * @return 当前对象
	 */
	public Select orderByDesc(String field) {
		return this.addOrderBy(field, true);
	}

	/**
	 * 设置查询的便宜量，从0开始。
	 * 
	 * @param offset
	 *            from的位置
	 * @return 当前对象
	 */
	public Select offset(int offset) {
		Asserts.isTrue(offset >= 0, "offset must bigger or equal than 0");
		this.offset = offset;
		return this;
	}

	/**
	 * 
	 * @param limit
	 *            返回的最大条数。
	 * @return 当前对象
	 */
	public Select limit(int limit) {
		Asserts.isTrue(limit >= 0, "limit must bigger or equal than 0");
		this.limit = limit;
		return this;
	}

	/**
	 * 
	 * @param columns
	 *            设置查询放回的列，列名是java中的字段名。如果不设，将返回所有的字段
	 * @return 当前对象
	 */
	public Select selectColumns(String... columns) {
		if (columns == null || columns.length == 0) {
			this.selectColumns = null;
			return this;
		}
		this.selectColumns = Arrays.asList(columns);
		return this;
	}

	/**
	 * 如果为false，就不会从缓存中加载数据
	 * 
	 * @param fromCache
	 *            默认为true。sumk.sql.fromCache=false可以将全局参数设为false
	 * @return 当前对象
	 */
	public Select fromCache(boolean fromCache) {
		this.fromCache = fromCache;
		return this;
	}

	/**
	 * 如果为false，查出的结果将不会用于更新缓存
	 * 
	 * @param toCache
	 *            该参数设为true的实际意义不大
	 * @return 当前对象
	 */
	public Select toCache(boolean toCache) {
		this.toCache = toCache;
		return this;
	}

	/**
	 * 设置相等的条件。本方法可以被多次执行。 src中的各个条件是and关系。不同src之间是or关系<BR>
	 * <B>注意：如果pojo是map类型，那么它的null值是有效条件</B>
	 * 
	 * @param src
	 *            map或pojo类型。
	 * @return 当前对象
	 */
	public Select addEqual(Object src) {
		this._addIn(src);
		return this;
	}

	/**
	 * 各个addEqual之间的条件是OR，如果要组装AND条件，请用addEqual(Object src)
	 * 
	 * @param field
	 *            字段名
	 * @param value
	 *            要查询的条件的值
	 * @return 当前对象
	 */
	public Select addEqual(String field, Object value) {
		this._addIn(Collections.singletonMap(field, value));
		return this;
	}

	/**
	 * 传入多个条件
	 * 
	 * @param ins
	 *            集合各元素之间是or关系，map中各个kv是and关系
	 * @return 当前对象
	 */
	public Select addEquals(Collection<Map<String, Object>> ins) {
		this.in.addAll(ins);
		return this;
	}

	/**
	 * 通过数据库主键列表查询主键，本方法只支持单主键类型。多主键请用addEqual()或addEquals()方法
	 * 
	 * @param ids
	 *            id列表
	 * @return 注意：调用本方法之前，要确保调用过tableClass()方法
	 */
	public Select byPrimaryId(Object... ids) {
		return byId(true, ids);
	}

	/**
	 * 通过redis主键列表查询主键，是addEquals()的快捷方式，本方法只支持单主键类型。多主键请用addEqual()或addEquals()
	 * 方法<BR>
	 * <B>注意：调用本方法之前，要确保调用过tableClass()方法</B>
	 * 
	 * @param ids
	 *            id列表
	 * @return 当前对象
	 * 
	 */
	public Select byCacheId(Object... ids) {
		return byId(false, ids);
	}

	private Select byId(boolean dbPrimary, Object... ids) {
		if (ids == null || ids.length == 0) {
			return this;
		}
		this.pojoMeta = this.parsePojoMeta(true);
		List<ColumnMeta> cms = dbPrimary ? this.pojoMeta.getPrimaryIDs() : this.pojoMeta.getCacheIDs();
		Asserts.isTrue(cms != null && cms.size() == 1,
				pojoMeta.getTableName() + " is not an one " + (dbPrimary ? "primary" : "cache") + " key table");
		String key = cms.get(0).getFieldName();
		for (Object id : ids) {
			addEqual(Collections.singletonMap(key, id));
		}
		return this;
	}

	public Select tableClass(Class<?> tableClass) {
		this.tableClass = tableClass;
		return this;
	}

	protected ResultHandler resultHandler() {
		return this.resultHandler == null ? PojoResultHandler.handler : this.resultHandler;
	}

	public <T> List<T> queryList() {
		try {
			ResultHandler handler = this.resultHandler();
			this.pojoMeta = this.parsePojoMeta(true);
			List<T> list = new ArrayList<>();
			List<Map<String, Object>> origin = this.in;
			Exchange exchange = new Exchange(origin);

			if (fromCache && this.selectColumns == null && _compare == null && this.orderby == null
					&& this.offset == 0) {
				exchange.findFromCache(pojoMeta);
				if (exchange.getData() != null && exchange.getData().size() > 0) {
					List<T> tmp = handler.parseFromJson(pojoMeta, exchange.getData());
					if (tmp != null && tmp.size() > 0) {
						list.addAll(tmp);
					}
				}
			}

			if (CollectionUtil.isNotEmpty(this.in) && CollectionUtil.isEmpty(exchange.getLeftIn())) {
				return list;
			}

			this.in = exchange.getLeftIn();
			List<T> dbData = handler.parse(pojoMeta, this.accept(visitor));
			this.in = origin;
			if (dbData == null || dbData.isEmpty()) {
				return list;
			}
			list.addAll(dbData);
			List<Map<String, Object>> eventIn = fromCache ? exchange.getLeftIn() : this.in;

			if (this.toCache && selectColumns == null && _compare == null && this.offset == 0
					&& (limit <= 0 || limit >= DBSettings.asNoLimit()) && CollectionUtil.isNotEmpty(eventIn)) {

				QueryEvent event = new QueryEvent(this.parsePojoMeta(true).getTableName());
				event.setIn(eventIn);
				event.setResult(dbData);
				DBEventPublisher.publish(event);
			}
			if (this.limit > 0 && list.size() > this.limit) {
				return list.subList(0, this.limit);
			}
			return list;
		} catch (Exception e) {
			throw SumkException.create(e);
		}
	}

	public <T> T queryOne() {
		List<T> list = this.queryList();
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 根据select的条件，查询符合条件的记录数。其中offset、limit、order by属性被过滤掉<BR>
	 * 这个方法可以在select执行前调用，也可以在select执行后调用
	 * 
	 * @return 符合条件的数据库记录数
	 */
	public int count() {
		return new Count(this).execute();
	}
}
