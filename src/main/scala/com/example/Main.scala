package com.example

import com.example.draw._
import com.example.game._
import com.example.userinput._

import zio._
import java.io.IOException
import zio.console._
import zio.clock._

object Main extends App {

  override def run(args: List[String]): ZIO[Console with Clock, Nothing, ExitCode] = {
    val game = new Game(20, 20, UserInput.moves)
    for {
      _ <- Screen.draw(Draw.eraseScreen)
      a <- game.gameStates.tap(s => Screen.render(s)).either.fold(ZIO.succeed(()))((x,y) => ZIO.succeed(())).exitCode
    } yield a
  }
}


