package io.nats.client

import java.util.concurrent.TimeUnit

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Milliseconds, Seconds, Span}
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

class SubTest extends FunSpec with Matchers with ScalaFutures {

	implicit val ec = ExecutionContext.global
	implicit val timeout = Duration(1L, TimeUnit.SECONDS)

	override implicit def patienceConfig = PatienceConfig(Span(2, Seconds), Span(50, Milliseconds))

	describe("with a subscription") {
		val conn = Nats.connect()
		val subscription = conn.subscribe("test")

		describe("we get implicits") {
			import Implicits._
			val sub = subscription.toScala

			it("that can fetch a message") {
				conn.publish("test", "Hello world!".getBytes("utf-8"))

				val result = sub.nextMessage

				whenReady(result) {
					case Some(msg) => {
						val text = new String(msg.getData, "utf-8")
						text shouldBe "Hello world!"
					}
					case None => fail("Expected a message")
				}
			}

			it("that can be drained") {
				val drain = sub.drain(timeout)

				whenReady(drain) {b =>
					b shouldBe true
				}

				val next = sub.nextMessage
		      .map(_ => "Fail")
			  	.recover {
					  case e:Throwable => e.getMessage
				  }

				whenReady(next){ msg =>
					msg shouldBe "This subscription is inactive."
				}
			}
		}
	}

}
