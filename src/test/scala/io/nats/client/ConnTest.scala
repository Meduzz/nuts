package io.nats.client

import java.util.concurrent.TimeUnit

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Milliseconds, Seconds, Span}
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.{ExecutionContext, duration}


class ConnTest extends FunSpec with Matchers with ScalaFutures {

	implicit val ec = ExecutionContext.global
	implicit val timeout = duration.Duration(1L, TimeUnit.SECONDS)

	override implicit def patienceConfig = PatienceConfig(Span(2, Seconds), Span(50, Milliseconds))

	describe("with a Connection") {
		val conn = Nats.connect()

		describe("we have access to implicits") {
			import Implicits._
			val nuts = conn.toScala

			it("like plain subscribe") {
				val dispatcher = nuts.subscribe("test", (msg) => {
					val text = new String(msg.getData, "utf-8")
					text shouldBe "Hello world!"
				})

				dispatcher.unsubscribe("test", 1)

				conn.publish("test", "Hello world!".getBytes("utf-8"))
			}

			it("like subscribe with queue") {
				val dispatcher = nuts.subscribe("test2", "test", (msg) => {
					val text = new String(msg.getData, "utf-8")
					text shouldBe "Hello world!"
				})

				dispatcher.unsubscribe("test2", 1)

				conn.publish("test2", "Hello world".getBytes("utf-8"))
			}

			it("and request that returns a futurue") {
				val dispatcher = nuts.subscribe("test3", (msg) => {
					conn.publish(msg.getReplyTo, msg.getData.reverse)
				})

				dispatcher.unsubscribe("test3", 1)

				val reply = nuts.request("test3", "Hello world!".getBytes("utf-8"))

				whenReady(reply) {
					case Some(rply) => {
						val text = new String(rply.getData, "utf-8")
						text shouldBe "Hello world!".reverse
					}
					case None => fail("Expected a reply")
				}
			}
		}

		describe("futures and failures") {
			it("when a request times out we get a none") {
				import Implicits._
				val nuts = conn.toScala

				val reply = nuts.request("test-1", "Hello world!".getBytes("utf-8"))

				whenReady(reply) { rply =>
					rply shouldBe None
				}
			}
		}
	}

}
