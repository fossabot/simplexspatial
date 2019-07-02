/*
 * Copyright 2019 SimplexPortal Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simplexportal.spatial.api

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.simplexportal.spatial.TileActor
import com.simplexportal.spatial.TileActor.{AddBatch, AddNode, AddWay, GetMetrics}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

class SimplexSpatialServiceImpl(tile: ActorRef)(implicit executionContext: ExecutionContext) extends SimplexSpatialService {

  // FIXME: Temporal timeout for POC
  implicit val timeout = Timeout(15 minutes)

  override def addNode(in: AddNodeCmd): Future[Done] = {
    tile ! AddNode(in.id, in.lat, in.lon, in.attributes)
    Future.successful(Done())
  }

  override def addWay(in: AddWayCmd): Future[Done] = {
    tile ! AddWay(in.id, in.nodeIds, in.attributes)
    Future.successful(Done())
  }

  override def executeBatch(in: ExecuteBatchCmd): Future[Done] = {
    val nodesToAdd = in.nodes.map( inNodes => AddNode(inNodes.id, inNodes.lat, inNodes.lon, inNodes.attributes))
    val waysToAdd = in.ways.map( inWay => AddWay(inWay.id, inWay.nodeIds, inWay.attributes))
    tile ! AddBatch(nodesToAdd ++ waysToAdd)
    Future.successful(Done())
  }

  override def getMetrics(in: GetMetricsCmd): Future[Metrics] = {
    println(s"Getting metrics")
    (tile ask GetMetrics).mapTo[TileActor.Metrics].map( m => Metrics(ways = m.ways, nodes = m.nodes))
  }
}