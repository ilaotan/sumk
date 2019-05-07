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
package org.yx.rpc.codec.decoders;

import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.yx.annotation.Bean;
import org.yx.rpc.RpcGson;
import org.yx.rpc.codec.Protocols;
import org.yx.rpc.codec.Request;

@Bean
public class JsonParamDecoder implements SumkMinaDecoder {

	@Override
	public boolean accept(int protocol) {
		return Protocols.hasFeature(protocol, Protocols.REQ_PARAM_JSON);
	}

	@Override
	public Request decode(int protocol, String message) throws ProtocolDecoderException {
		String[] msgs = message.split(Protocols.LINE_SPLIT, -1);
		if (msgs.length < 2) {
			throw new ProtocolDecoderException("error jsoned param req");
		}
		Request req = RpcGson.fromJson(msgs[0], Request.class);
		req.setJsonedParam(msgs[1]);
		req.protocol(protocol);
		return req;
	}

}
