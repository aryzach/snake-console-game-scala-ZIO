package com.example

import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.all._
import fs2.Stream
import com.example.draw._
import com.example.game._
import com.example.userinput._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val game = new Game(1,2, UserInput.moves)
    Console.draw(Draw.eraseScreen) *> 
    game.gameStates.flatMap(s => Stream.eval(Console.render(s))).compile.drain *>
    IO.pure(ExitCode.Success)


  }


}


