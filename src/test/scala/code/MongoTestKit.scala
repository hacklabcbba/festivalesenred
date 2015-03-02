package code

import code.config.MongoConfig
import code.model._
import code.model.contact._
import code.model.development._
import code.model.document._
import code.model.festival._
import code.model.field._
import code.model.institution._
import code.model.link._
import code.model.proposal._
import net.liftweb.mongodb.record.MongoMetaRecord
import org.scalatest._

import net.liftweb._
import common._
import http._
import mongodb._
import util._
import util.Helpers.randomString
import scala.collection.JavaConverters._

import com.mongodb.{BasicDBObject, MongoClient, ServerAddress}

// The sole mongo object for testing
object TestMongo {
  val mongo = new MongoClient(new ServerAddress(
    Props.get("mongo.default.host", "127.0.0.1"),
    Props.getInt("mongo.default.port", 27017)
  ))
}

/**
 * Creates a `ConnectionIdentifier` and database named after the class.
 * Therefore, each Suite class shares the same database.
 * Database is dropped after all tests have been run in the suite.
 */
trait MongoBeforeAndAfterAll extends BeforeAndAfterAll {
  this: Suite =>

  lazy val dbName = Props.get("mongo.default.name", "verbal-test")

  def debug = false // db won't be dropped if this is true

  lazy val identifier = {
    MongoConfig.init()
    MongoConfig.defaultId.vend
  }

  override def beforeAll(configMap: ConfigMap) {
    // define the db
    MongoConfig.init()
  }

  override def afterAll(configMap: ConfigMap) {
    if (!debug) { destroyDb() }
  }

  lazy val collections: List[MongoMetaRecord[_]] =
    List(Contact, Development, City, Festival, Institution, Link)

  def destroyDb(): Unit = {
    collections.foreach(_ bulkDelete_!! new BasicDBObject)
  }
}

/**
 * Basic Mongo suite for running Mongo tests.
 */
trait MongoSuite extends SuiteMixin with MongoBeforeAndAfterAll {
  this: Suite =>

  def mongoIdentifier: StackableMaker[ConnectionIdentifier]

  abstract override def withFixture(test: NoArgTest) = {
    MongoConfig.init()
    mongoIdentifier.doWith(MongoConfig.defaultId.vend) {
      super.withFixture(test)
    }
  }
}

/**
 * Mongo suite running within a LiftSession.
 */
trait MongoSessionSuite extends SuiteMixin with MongoBeforeAndAfterAll {
  this: Suite =>

  def mongoIdentifier: StackableMaker[ConnectionIdentifier]

  // Override with `val` to share session amongst tests.
  protected def session = new LiftSession("", randomString(20), Empty)

  abstract override def withFixture(test: NoArgTest) = {
    S.initIfUninitted(session) {
      MongoConfig.init()
      mongoIdentifier.doWith(MongoConfig.defaultId.vend) {
        super.withFixture(test)
      }
    }
  }
}
