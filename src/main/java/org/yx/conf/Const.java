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
package org.yx.conf;

import org.objectweb.asm.Opcodes;

public interface Const {
	int DEFAULT_ORDER = 100;

	int JVM_VERSION = Opcodes.V1_8;

	int ASM_VERSION = Opcodes.ASM5;

	String DEFAULT_DB_NAME = "sumk";

	int DEFAULT_INTF_PREFIX_PART_COUNT = 3;

	String SOA_SESSION_IDLE = "sumk.rpc.session.idle";
}
