name := "nuts"

version := "20190405"

scalaVersion := "2.12.8"

organization := "se.kodiak.tools"

credentials += Credentials(Path.userHome / ".ivy2" / ".tools")

publishTo := Some("se.kodiak.tools" at "https://yamr.kodiak.se/maven")

publishArtifact in (Compile, packageDoc) := false

libraryDependencies ++= Seq(
	"io.nats" % "jnats" % "2.2.0",
	"org.scalatest" %% "scalatest" % "3.0.0" % "test"
)