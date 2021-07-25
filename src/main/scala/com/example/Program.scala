package com.example

import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.all._
import fs2.Stream
import com.example.draw._
import com.example.game._
import com.example.userinput._

object Program extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val game = new Game(1,2, UserInput.moves)

    Console.draw(Draw.eraseScreen) *>
    game.gameStates.flatMap(r => Stream.eval(printState(r))).compile.drain *>
    IO.pure(ExitCode.Success)
  }

  private def printState(state: GameState): IO[Unit] =
    Console.draw(
      Draw.printAt(2, 2, s"Score: ${state.char.toString}") *>

      Draw.goto(1 + 1, 0)
    )   

}


