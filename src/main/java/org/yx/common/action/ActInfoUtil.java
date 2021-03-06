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
package org.yx.common.action;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yx.annotation.Param;
import org.yx.common.context.CalleeNode;
import org.yx.conf.AppInfo;
import org.yx.util.S;
import org.yx.util.SumkDate;
import org.yx.validate.FieldParameterHolder;
import org.yx.validate.FieldParameterInfo;
import org.yx.validate.ManuParameterInfo;
import org.yx.validate.ParameterInfo;

public final class ActInfoUtil {

	public static Object describe(Class<?> clazz) {
		if (clazz.isArray()) {
			return new Object[] { describe(clazz.getComponentType()) };
		}
		if (clazz.isPrimitive() || clazz.getName().startsWith("java.") || clazz == SumkDate.class) {
			return clazz.getSimpleName();
		}
		if (Map.class.isAssignableFrom(clazz)) {
			return Collections.emptyMap();
		}
		if (Collection.class.isAssignableFrom(clazz)) {
			return Collections.emptyList();
		}
		Map<String, Object> map = new LinkedHashMap<>();
		Field[] fs = S.bean().getFields(clazz);
		if (AppInfo.getBoolean("sumk.http.act.output.class", false)) {
			map.put("$class", clazz.getName());
		}
		for (Field f : fs) {
			map.putIfAbsent(f.getName(), describe(f.getType()));
		}
		return map;
	}

	private static Map<String, Object> createMap(String name, CalleeNode node) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("name", name);
		map.put("class", node.getDeclaringClass().getName());
		map.put("method", node.getMethodName());
		map.put("resultType", node.rawMethod().getGenericReturnType().getTypeName());
		return map;
	}

	public static Map<String, Object> simpleInfoMap(String name, CalleeNode node) {
		Map<String, Object> map = createMap(name, node);
		Map<String, Object> param = new LinkedHashMap<>();
		int paramSize = node.argNames() == null ? 0 : node.argNames().size();
		Class<?>[] paramTypes = node.getParameterTypes();
		for (int i = 0; i < paramSize; i++) {
			Class<?> paramType = paramTypes[i];
			param.put(node.argNames().get(i), describe(paramType));
		}
		map.put("params", param);
		map.put("result", describe(node.getReturnType()));
		return map;
	}

	public static Map<String, Object> fullInfoMap(String name, CalleeNode node) {
		Map<String, Object> map = createMap(name, node);
		List<Object> list = new ArrayList<>();
		if (node.paramInfos() != null) {
			int paramSize = node.argNames() == null ? 0 : node.argNames().size();
			Class<?>[] paramTypes = node.getParameterTypes();
			for (int i = 0; i < paramSize; i++) {
				Class<?> paramType = paramTypes[i];
				ParameterInfo pi = node.paramInfos().get(i);
				if (pi == null) {
					ManuParameterInfo mp = new ManuParameterInfo();
					mp.setParamType(paramType);
					mp.setComplex(false);
					mp.setParamName(node.argNames().get(i));
					pi = mp;
				}
				boolean supportComplex = pi == null ? false : pi.isComplex();
				ParamDescript pd = fullDescribe(paramType, pi, supportComplex);
				list.add(pd);
			}
		}
		map.put("params", list);
		Class<?> returnClz = node.getReturnType();
		ManuParameterInfo mp = new ManuParameterInfo();
		mp.setParamType(returnClz);
		if (!returnClz.isPrimitive()) {
			mp.setComplex(true);
		}
		map.put("result", fullDescribe(returnClz, mp, false));
		return map;
	}

	public static ParamDescript fullDescribe(Class<?> clazz, ParameterInfo info, boolean supportComplex) {
		if (clazz.isArray()) {
			ParamDescript pd = fullDescribe(clazz.getComponentType(), info, supportComplex);
			pd.setType(pd.getType() + "[]");
			pd.setArray(true);
			return pd;
		}
		ParamDescript pd = new ParamDescript().copyFrom(info, supportComplex).setType(clazz.getName());
		if (clazz.isPrimitive() || clazz.getName().startsWith("java.") || clazz == SumkDate.class
				|| Map.class.isAssignableFrom(clazz) || Collection.class.isAssignableFrom(clazz)) {
			return pd.setType(clazz.getName());
		}
		List<ParamDescript> list = new ArrayList<>();
		Field[] fs = S.bean().getFields(clazz);
		Map<Field, FieldParameterInfo> infoMap = FieldParameterHolder.getFieldParameterMap(clazz);
		for (Field f : fs) {
			ParameterInfo fp = infoMap.get(f);
			if (fp == null) {
				Param param = f.getAnnotation(Param.class);
				if (param != null) {
					fp = new FieldParameterInfo(param, f);
				} else {
					ManuParameterInfo mp = new ManuParameterInfo();
					mp.setParamName(f.getName());
					mp.setParamType(f.getType());
					fp = mp;
				}
			}

			list.add(fullDescribe(f.getType(), fp, supportComplex && fp.isComplex()));
		}
		pd.setComplexFields(list);
		return pd;
	}
}
