package io.nats.client

import java.time

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

class Conn(val conn:Connection) {
	def request(subject:String, data:Array[Byte])(implicit ec:ExecutionContext, timeout:Duration):Future[Message] = {
		val javaTimeout = time.Duration.ofNanos(timeout.toNanos)
		Future(conn.request(subject, data, javaTimeout))
	}

	def request(subject:String, data:String, encoding:String = "utf-8")(implicit ec:ExecutionContext, timeout:Duration):Future[Message] = {
		val javaTimeout = time.Duration.ofNanos(timeout.toNanos)
		Future(conn.request(subject, data.getBytes(encoding), javaTimeout))
	}

	def subscribe(subject:String, handler:Message => Unit):Dispatcher = {
		val dispatcher = conn.createDispatcher((msg:Message) => handler(msg))
		dispatcher.subscribe(subject)
	}

	def subscribe(subject:String, queue:String, handler:Message => Unit):Dispatcher = {
		val dispatcher = conn.createDispatcher((msg:Message) => handler(msg))
		dispatcher.subscribe(subject, queue)
	}

	def publish(subject:String, replyTo:String, body:Array[Byte]):Unit = conn.publish(subject, replyTo, body)

	def closeDispatcher(dispatcher:Dispatcher):Unit = conn.closeDispatcher(dispatcher)

	def publish(subject:String, body:Array[Byte]):Unit = conn.publish(subject, body)

	def publish(subject:String, body:String, encoding:String = "utf-8"):Unit = conn.publish(subject, body.getBytes(encoding))

	def toScala:Conn = this
}
