name := "nuts"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
	"io.nats" % "jnats" % "2.2.0",
	"org.scalatest" %% "scalatest" % "3.0.0" % "test"
)