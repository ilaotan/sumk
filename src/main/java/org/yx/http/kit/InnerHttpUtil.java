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
package org.yx.http.kit;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.BiConsumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.yx.common.action.ActStatis;
import org.yx.common.context.ActionContext;
import org.yx.common.sumk.UnsafeByteArrayOutputStream;
import org.yx.conf.AppInfo;
import org.yx.http.HttpErrorCode;
import org.yx.log.Logs;
import org.yx.util.S;
import org.yx.util.UUIDSeed;

public final class InnerHttpUtil {
	private static HttpKit kit = new DefaultHttpKit();
	private static BiConsumer<HttpServletRequest, HttpServletResponse> optionMethodHandler;

	public static BiConsumer<HttpServletRequest, HttpServletResponse> getOptionMethodHandler() {
		return optionMethodHandler;
	}

	public static void setOptionMethodHandler(BiConsumer<HttpServletRequest, HttpServletResponse> optionMethodHandler) {
		InnerHttpUtil.optionMethodHandler = optionMethodHandler;
	}

	public static HttpKit getKit() {
		return kit;
	}

	public static void setKit(HttpKit kit) {
		InnerHttpUtil.kit = Objects.requireNonNull(kit);
	}

	public static byte[] extractData(InputStream in, int expectSize) throws IOException {
		int count = 0;
		int n = 0;
		expectSize = kit.expectReqDataSize(expectSize);
		if (Logs.http().isTraceEnabled()) {
			Logs.http().trace("expect request content length: {}", expectSize);
		}
		byte[] temp = new byte[512];
		@SuppressWarnings("resource")
		UnsafeByteArrayOutputStream output = new UnsafeByteArrayOutputStream(expectSize);
		while (-1 != (n = in.read(temp))) {
			output.write(temp, 0, n);
			count += n;
			if (count > HttpSettings.maxHttpBody()) {
				throw HttpException.create(HttpErrorCode.BODY_TOO_BIG, "请求数据太长");
			}
		}
		byte[] bs = output.extractHttpBodyData();
		output.close();
		return bs;
	}

	public static Charset charset(HttpServletRequest req) {
		return kit.charset(req);
	}

	public static void noCache(HttpServletResponse resp) {
		kit.noCache(resp);
	}

	public static void record(String act, long time, boolean isSuccess) {
		kit.record(act, time, isSuccess);
	}

	public static ActStatis getActStatis() {
		return kit.actStatis();
	}

	public static void sendError(HttpServletResponse resp, int code, String message, Charset charset) {
		try {
			kit.sendError(resp, code, message, charset);
		} catch (IOException e) {
			Logs.http().error(e.getLocalizedMessage(), e);
		}
	}

	public static void setRespHeader(HttpServletResponse resp, Charset charset) throws IOException {
		kit.setRespHeader(resp, charset);
	}

	public static boolean preServerHandle(HttpServletRequest req, HttpServletResponse resp, String firstKey) {
		InnerHttpUtil.noCache(resp);
		resp.setContentType("text/plain;charset=UTF-8");
		String md5 = AppInfo.get(firstKey, "sumk.union.monitor", "61c72b1ce5858d83c90ba7b5b1096697");
		String sign = req.getParameter("sign");
		if (sign == null) {
			Logs.http().debug("sign is empty");
			return false;
		}
		try {
			String signed = S.hash().digest(sign, StandardCharsets.UTF_8);
			if (!md5.equalsIgnoreCase(signed)) {
				Logs.http().debug("signed:{},need:{}", signed, md5);
				return false;
			}
		} catch (Exception e) {
		}
		return true;
	}

	public static void startContext(HttpServletRequest req, HttpServletResponse resp, String act) {
		String traceId = UUIDSeed.seq18();
		ActionContext.newContext(act, traceId, req.getParameter("thisIsTest"));
		resp.setHeader(HttpSettings.traceHeaderName(), traceId);
	}
}
