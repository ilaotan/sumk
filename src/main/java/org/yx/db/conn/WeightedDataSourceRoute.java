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
package org.yx.db.conn;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.yx.common.route.WeightedRoute;

class WeightedDataSourceRoute extends WeightedRoute<WeightedDS> {

	public WeightedDataSourceRoute(Collection<WeightedDS> servers) {
		super(servers.toArray(new WeightedDS[servers.size()]));
	}

	public Set<DataSource> allDataSource() {
		Set<DataSource> list = new HashSet<>();
		for (WeightedDS ds : this.SERVERS) {
			list.add(ds.getDs());
		}
		return list;
	}

	public SumkDataSource datasource() {
		WeightedDS sm = this.getServer();
		if (sm == null) {
			return null;
		}
		return sm.getDs();
	}

	@Override
	protected boolean isDowned(WeightedDS server) {
		return false;
	}

}
