import sbt._

object Dependencies {

  val liftVersion = "2.6.2"
  val scalazVersion = "7.1.6"

  val argonaut         = "io.argonaut"                   %% "argonaut"                  % "6.1"
  val liftWebkit       = "net.liftweb"                   %% "lift-webkit"               % liftVersion
  val liftCommon       = "net.liftweb"                   %% "lift-common"               % liftVersion
  val jodaTime         = "joda-time"                      % "joda-time"                 % "2.9.1"
  val jodaTimeConvert  = "org.joda"                       % "joda-convert"              % "1.8.1"
  val scalazCore       = "org.scalaz"                    %% "scalaz-core"               % scalazVersion
  val pimpathon        = "com.github.stacycurl"          %% "pimpathon-core"            % "1.5.20"
  val servletApi       = "javax.servlet"                  % "javax.servlet-api"         % "3.1.0"
}
