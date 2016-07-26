import sbt._

object Dependencies {

  val liftVersion = "2.6.2"
  val scalazVersion = "7.1.6"
  val http4sVersion = "0.12.3"

  val argonaut         = "io.argonaut"                   %% "argonaut"                  % "6.1"
  val liftWebkit       = "net.liftweb"                   %% "lift-webkit"               % liftVersion
  val liftCommon       = "net.liftweb"                   %% "lift-common"               % liftVersion
  val jodaTime         = "joda-time"                      % "joda-time"                 % "2.9.1"
  val jodaTimeConvert  = "org.joda"                       % "joda-convert"              % "1.8.1"
  val scalazCore       = "org.scalaz"                    %% "scalaz-core"               % scalazVersion
  val http4sCore       = "org.http4s"                    %% "http4s-core"               % http4sVersion
  val http4sDsl        = "org.http4s"                    %% "http4s-dsl"                % http4sVersion
  val naiveHttp        = "io.shaka"                      %% "naive-http"                % "73"
}
