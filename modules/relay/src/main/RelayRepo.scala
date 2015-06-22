package lila.relay

import org.joda.time.DateTime
import reactivemongo.bson._
import reactivemongo.core.commands._

import BSONHandlers._
import lila.db.BSON._
import lila.db.Implicits._

private final class RelayRepo(coll: Coll) {

  private def selectId(id: String) = BSONDocument("_id" -> id)
  private def selectFicsIdName(ficsId: Int, name: String) = BSONDocument(
    "ficsId" -> ficsId,
    "name" -> name)

  def byId(id: String): Fu[Option[Relay]] = coll.find(selectId(id)).one[Relay]

  def byFicsIdName(ficsId: Int, name: String): Fu[Option[Relay]] =
    coll.find(selectFicsIdName(ficsId, name)).one[Relay]

  def upsert(ficsId: Int, name: String, status: Relay.Status) =
    byFicsIdName(ficsId, name) flatMap {
      case None        => coll insert Relay.make(ficsId, name, status)
      case Some(relay) => coll.update(selectId(relay.id), relay.copy(status = status))
    } void
}
