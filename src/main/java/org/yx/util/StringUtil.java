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
package org.yx.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.yx.exception.SumkException;

public final class StringUtil {

	public static String[] splitByComma(String text) {
		return text.replace('，', ',').split(",");
	}

	public static String uncapitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		return new StringBuilder(strLen).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1))
				.toString();
	}

	public static boolean isEmpty(CharSequence str) {
		return str == null || str.length() == 0;
	}

	public static String toLatin(String v) {
		if (v == null || v.isEmpty()) {
			return v;
		}
		return v.replace('，', ',').replace('；', ';').replace('　', ' ').replace('：', ':').replace('。', '.').replace('？',
				'?');
	}

	public static String capitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		return new StringBuilder(strLen).append(Character.toUpperCase(str.charAt(0))).append(str.substring(1))
				.toString();
	}

	public static boolean isNotEmpty(CharSequence str) {
		return str != null && str.length() > 0;
	}

	public static String load(InputStream in) throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
			char[] buf = new char[1024];
			int len = 0;
			StringBuilder sb = new StringBuilder();
			while ((len = reader.read(buf)) > -1) {
				if (len == 0) {
					continue;
				}
				sb.append(buf, 0, len);
			}
			return sb.toString();
		} finally {
			in.close();
		}

	}

	public static String camelToUnderline(String param) {
		if (param == null) {
			return param;
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len + 10);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (i == 0) {
				sb.append(Character.toLowerCase(c));
				continue;
			}
			if (Character.isUpperCase(c)) {
				sb.append('_');
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static boolean isNumber(char c) {
		return c >= '0' && c <= '9';
	}

	public static String requireNotEmpty(String text) {
		if (text == null) {
			throw new SumkException(652342134, "字符串是null");
		}
		text = text.trim();
		if (text.isEmpty()) {
			throw new SumkException(652342134, "字符串是空的");
		}
		return text;
	}
}
