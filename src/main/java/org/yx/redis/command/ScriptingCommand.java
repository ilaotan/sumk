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
package org.yx.redis.command;

import java.util.List;

public interface ScriptingCommand {
	Object eval(String script, int keyCount, String... params);

	Object eval(String script, List<String> keys, List<String> args);

	Object eval(String script);

	Object evalsha(String sha1);

	Object evalsha(String sha1, List<String> keys, List<String> args);

	Object evalsha(String sha1, int keyCount, String... params);

	Boolean scriptExists(String sha1);

	List<Boolean> scriptExists(String... sha1);

	String scriptLoad(String script);
}