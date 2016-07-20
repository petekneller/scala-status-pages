name := "status-pages-http4s"

import Dependencies._

libraryDependencies ++= Seq(
  scalazCore,
  jodaTime,
  argonaut,
  http4sCore,
  http4sDsl
)
