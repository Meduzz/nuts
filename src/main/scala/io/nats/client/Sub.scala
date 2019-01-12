package io.nats.client

import java.{lang, time}

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future, Promise}


class Sub(val subscription: Subscription) {
	def nextMessage(implicit ec:ExecutionContext, timeout:Duration):Future[Option[Message]] = {
		val javaTimeout =  time.Duration.ofNanos(timeout.toNanos)
		Future(Option(subscription.nextMessage(javaTimeout)))
	}

	def drain(timeout:Duration):Future[Boolean] = {
		val javaTimeout =  time.Duration.ofNanos(timeout.toNanos)
		val p = Promise[Boolean]()

		subscription.drain(javaTimeout)
	  	.whenComplete((t:lang.Boolean, u:Throwable) => {
			  if (u != null) {
				  p.failure(u)
			  } else {
				  p.success(t)
			  }
		  })

		p.future
	}

	def isActive:Boolean = subscription.isActive

	def unsubscribe():Unit = subscription.unsubscribe()

	def unsubscribe(after:Int):Subscription = subscription.unsubscribe(after)

	def toScala:Sub = this
}
