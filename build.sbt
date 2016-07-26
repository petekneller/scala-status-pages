organization in ThisBuild := "com.github.petekneller"

scalaVersion in ThisBuild := "2.11.8"

version in ThisBuild := "dev"

name := "status-pages"

resolvers in ThisBuild += "Tim Tennant's repo" at "http://dl.bintray.com/timt/repo/"

lazy val core = project in file("core")

lazy val liftweb = (project in file("liftweb")).dependsOn(core)

lazy val http4s = (project in file("http4s")).dependsOn(core)

lazy val naive = (project in file("naive")).dependsOn(core)

lazy val root = (project in file(".")).aggregate(core, liftweb, http4s, naive)
