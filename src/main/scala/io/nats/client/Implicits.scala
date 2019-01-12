package io.nats.client

object Implicits {
	implicit def asConn(conn:Connection):Conn = new Conn(conn)
	implicit def asSub(sub:Subscription):Sub = new Sub(sub)
}
