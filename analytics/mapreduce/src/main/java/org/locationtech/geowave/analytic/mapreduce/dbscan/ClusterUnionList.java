/*******************************************************************************
 * Copyright (c) 2013-2018 Contributors to the Eclipse Foundation
 *   
 *  See the NOTICE file distributed with this work for additional
 *  information regarding copyright ownership.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Apache License,
 *  Version 2.0 which accompanies this distribution and is available at
 *  http://www.apache.org/licenses/LICENSE-2.0.txt
 ******************************************************************************/
package org.locationtech.geowave.analytic.mapreduce.dbscan;

import java.util.Map;

import org.locationtech.geowave.analytic.nn.DistanceProfile;
import org.locationtech.geowave.analytic.nn.NeighborList;
import org.locationtech.geowave.analytic.nn.NeighborListFactory;
import org.locationtech.geowave.core.index.ByteArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * A cluster represented by a hull.
 * 
 * Intended to run in a single thread. Not Thread Safe.
 * 
 * 
 * TODO: connectGeometryTool.connect(
 */
public class ClusterUnionList extends
		DBScanClusterList implements
		Cluster
{

	protected static final Logger LOGGER = LoggerFactory.getLogger(ClusterUnionList.class);

	public ClusterUnionList(
			final ByteArray centerId,
			final ClusterItem center,
			final NeighborListFactory<ClusterItem> factory,
			final Map<ByteArray, Cluster> index ) {
		super(
				center.getGeometry(),
				(int) center.getCount(),
				centerId,
				index);
	}

	protected long addAndFetchCount(
			final ByteArray id,
			final ClusterItem newInstance,
			final DistanceProfile<?> distanceProfile ) {
		return 0;
	}

	@Override
	public void merge(
			final Cluster cluster ) {
		super.merge(cluster);
		if (cluster != this) {
			union(((DBScanClusterList) cluster).clusterGeo);
		}
	}

	public boolean isCompressed() {
		return true;
	}

	protected Geometry compress() {
		return clusterGeo;
	}

	public static class ClusterUnionListFactory implements
			NeighborListFactory<ClusterItem>
	{
		private final Map<ByteArray, Cluster> index;

		public ClusterUnionListFactory(
				final Map<ByteArray, Cluster> index ) {
			super();
			this.index = index;
		}

		public NeighborList<ClusterItem> buildNeighborList(
				final ByteArray centerId,
				final ClusterItem center ) {
			Cluster list = index.get(centerId);
			if (list == null) {
				list = new ClusterUnionList(
						centerId,
						center,
						this,
						index);
			}
			return list;
		}
	}
}
