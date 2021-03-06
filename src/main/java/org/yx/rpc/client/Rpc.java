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
package org.yx.rpc.client;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.yx.conf.AppInfo;
import org.yx.exception.SumkException;
import org.yx.log.Logs;
import org.yx.main.StartContext;
import org.yx.main.SumkThreadPool;
import org.yx.rpc.RpcSettings;
import org.yx.rpc.client.route.ZkRouteParser;
import org.yx.validate.Validators;

public final class Rpc {
	private Rpc() {
	}

	private static volatile boolean strated;

	private static String appId;

	static String appId() {
		return appId;
	}

	private static ExecutorService clientExecutor = SumkThreadPool.executor();

	public static ExecutorService clientExecutor() {
		return clientExecutor;
	}

	public static void resetStatus() {
		strated = false;
	}

	public static synchronized void init() {
		if (strated) {
			return;
		}
		try {
			appId = AppInfo.appId("sumk");
			RpcSettings.init();
			Validators.init();
			Rpc.clientExecutor = StartContext.inst().getExecutorService("sumk.rpc.client.executor");
			String zkUrl = AppInfo.getClinetZKUrl();
			Logs.rpc().info("rpc client zkUrl:{}", zkUrl);
			ZkRouteParser.get(zkUrl).readRouteAndListen();
			ReqSession.init();
			strated = true;
		} catch (Exception e) {
			throw SumkException.wrap(e);
		}
	}

	public static Client create(String api) {
		return new Client(api);
	}

	/**
	 * 根据参数顺序调用rpc方法<BR>
	 * 参数对象是原始的参数对象，不需要进行gson转化
	 * 
	 * @param api
	 *            注册的接口名称，如a.b.c
	 * @param args
	 *            支持泛型，比如List&lt;Integer&gt;之类的。但不提倡使用泛型。
	 * @return json格式的服务器响应结果
	 * @throws org.yx.exception.SoaException
	 *             rpc异常
	 * @throws org.yx.exception.BizException
	 *             业务异常
	 */
	public static String call(String api, Object... args) {
		return callAsync(api, args).getOrException();
	}

	/**
	 * 
	 * @param api
	 *            注册的接口名称，如a.b.c
	 * @param json
	 *            用json序列化的参数对象
	 * @return json格式的服务器响应结果
	 * @throws org.yx.exception.SoaException
	 *             rpc异常
	 * @throws org.yx.exception.BizException
	 *             业务异常
	 */
	public static String callInJson(String api, String json) {
		return callInJsonAsync(api, json).getOrException();
	}

	public static String callInMap(String api, Map<String, ?> map) {
		return callInMapAsync(api, map).getOrException();
	}

	/**
	 * 根据参数顺序<b>异步</b>调用rpc方法
	 * 
	 * @param api
	 *            注册的接口名称，如a.b.c
	 * @param args
	 *            支持泛型，比如List&lt;Integer&gt;之类的。但不提倡使用泛型
	 * @return json格式的服务器响应结果
	 */
	public static RpcFuture callAsync(String api, Object... args) {
		return create(api).paramInArray(args).execute();
	}

	/**
	 * 
	 * @param api
	 *            注册的接口名称，如a.b.c
	 * @param json
	 *            用json序列化的参数对象
	 * @return json格式的服务器响应结果
	 */
	public static RpcFuture callInJsonAsync(String api, String json) {
		return create(api).paramInJson(json).execute();
	}

	public static RpcFuture callInMapAsync(String api, Map<String, ?> map) {
		return create(api).paramInMap(map).execute();
	}

}
