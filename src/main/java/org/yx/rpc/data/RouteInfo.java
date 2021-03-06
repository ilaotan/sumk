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
package org.yx.rpc.data;

import java.util.ArrayList;
import java.util.Collection;

import org.yx.common.Host;
import org.yx.util.StringUtil;

public class RouteInfo {
	private Collection<IntfInfo> intfs = new ArrayList<IntfInfo>();
	private int weight;
	private int clientCount;
	private final Host host;

	private int feature;

	public RouteInfo(Host url) {
		this.host = url;
	}

	public Collection<IntfInfo> intfs() {
		return intfs;
	}

	void addIntf(IntfInfo intf) {
		this.intfs.add(intf);
	}

	void setWeight(String w) {
		if (StringUtil.isEmpty(w)) {
			return;
		}
		this.weight = Integer.parseInt(w);
	}

	void setClientCount(String w) {
		if (StringUtil.isEmpty(w)) {
			return;
		}
		this.clientCount = Integer.parseInt(w);
	}

	public int weight() {
		return this.weight;
	}

	public int clientCount() {
		return this.clientCount;
	}

	public Host host() {
		return host;
	}

	public int getFeature() {
		return feature;
	}

	void setFeature(int feature) {
		this.feature = feature;
	}
}
