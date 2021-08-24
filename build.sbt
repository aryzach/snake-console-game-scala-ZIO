import Dependencies._

val zioVersion = "1.0.4"


lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaVersion := "2.11.12",
    )), 
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-prelude" % "1.0.0-RC5",
      "dev.zio" %% "zio" % zioVersion,
      scalaTest % Test,
      fs2,
      cats,
      catsEffect,
      jansi,
      jline
    ),  
    scalacOptions ++= Seq(
      "-Ypartial-unification",
      "-Xfatal-warnings",
      "-language:higherKinds"
    )   
  )

