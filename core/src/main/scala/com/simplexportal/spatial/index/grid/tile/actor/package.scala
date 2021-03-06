/*
 * Copyright 2019 SimplexPortal Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.simplexportal.spatial.index.grid.tile.actor

import akka.actor.typed.ActorRef
import com.simplexportal.spatial.index.grid.CommonInternalSerializer
import com.simplexportal.spatial.index.grid.tile.impl.{NearestNode, TileIndex}
import com.simplexportal.spatial.model.{Location, Node, Way}

sealed trait Message extends CommonInternalSerializer
sealed trait Reply extends Message
sealed trait Command extends Message
sealed trait Query extends Command
sealed trait Action extends Command
protected[tile] sealed trait Event extends Message

case class Metrics(ways: Long, nodes: Long) extends Reply

trait ACK extends Reply

case class Done() extends ACK

case class NotDone(msg: String = "No error message") extends ACK

case class GetInternalNearestNodeResponse(
    tileId: String,
    origin: Location,
    nodes: Option[NearestNode]
) extends Reply

case class GetInternalNodeResponse(
    id: Long,
    node: Option[TileIndex.InternalNode]
) extends Reply

case class GetNodeResponse(
    id: Long,
    node: Option[Node]
) extends Reply

case class GetInternalNodesResponse(nodes: Seq[GetInternalNodeResponse])
    extends Reply

case class GetInternalWayResponse(
    id: Long,
    way: Option[TileIndex.InternalWay]
) extends Reply

case class GetWayResponse(
    id: Long,
    way: Option[Way]
) extends Reply

final case class GetNearestNode(
    location: Location,
    replyTo: ActorRef[GetInternalNearestNodeResponse]
) extends Query

final case class GetInternalNode(
    id: Long,
    replyTo: ActorRef[GetInternalNodeResponse]
) extends Query

final case class GetNode(
    id: Long,
    replyTo: ActorRef[GetNodeResponse]
) extends Query

final case class GetInternalNodes(
    ids: Seq[Long],
    replyTo: ActorRef[GetInternalNodesResponse]
) extends Query

final case class GetInternalWay(
    id: Long,
    replyTo: ActorRef[GetInternalWayResponse]
) extends Query

final case class GetWay(
    id: Long,
    replyTo: ActorRef[GetWayResponse]
) extends Query

final case class GetMetrics(replyTo: ActorRef[Metrics]) extends Query

sealed trait BatchActions extends Action

final case class AddNode(
    id: Long,
    lat: Double,
    lon: Double,
    attributes: Map[String, String],
    replyTo: Option[ActorRef[ACK]] = None
) extends BatchActions

final case class AddWay(
    id: Long,
    nodeIds: Seq[Long],
    attributes: Map[String, String],
    replyTo: Option[ActorRef[ACK]] = None
) extends BatchActions

final case class AddBatch(
    cmds: Seq[BatchActions],
    replyTo: Option[ActorRef[ACK]] = None
) extends Action

protected[tile] sealed trait AtomicEvent extends Event

protected[tile] final case class NodeAdded(
    id: Long,
    lat: Double,
    lon: Double,
    attributes: Map[String, String]
) extends AtomicEvent

protected[tile] final case class WayAdded(
    id: Long,
    nodeIds: Seq[Long],
    attributes: Map[String, String]
) extends AtomicEvent

protected[tile] final case class BatchAdded(events: Seq[AtomicEvent])
    extends Event
