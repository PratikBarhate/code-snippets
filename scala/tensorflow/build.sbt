name := "tensorflow-sample"
version := "0.1"
scalaVersion := "2.12.3"

// Managing dependencies
libraryDependencies ++= Seq(
  "org.platanios" %% "tensorflow" % "0.1.1" % "compile" classifier "darwin-cpu-x86_64"
)

// Uber jar settings
val meta = """META.INF(.)*""".r

assemblyMergeStrategy in assembly := {
  case "application.conf" => MergeStrategy.concat
  case meta(_) => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
