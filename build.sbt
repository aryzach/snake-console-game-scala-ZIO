import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaVersion := "2.11.12",
    )), 
    libraryDependencies ++= Seq(
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

