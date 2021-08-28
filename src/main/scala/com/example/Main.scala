package com.example

import com.example.draw._
import com.example.game._
import com.example.userinput._

import zio._
import java.io.IOException
import zio.console._
import zio.clock._
import zio.stream._
import zio.random._

object Main extends App {

  override def run(args: List[String]): ZIO[Console with Clock with Random, Nothing, ExitCode] = {
    val game = new Game(20, 20, UserInput.moves)
    for {
      _ <- Screen.draw(Draw.eraseScreen)
      a <- game.gameStates.tap(Screen.render).runDrain.exitCode
    } yield a

  }
}

/*
// for discord
sealed case class GameState (parts: [Coord])
  override def run(args: List[String]): ZIO[Console with Clock, Nothing, ExitCode] = {
    val streamOfState: ZStream[Console with Clock, IOException, GameState]
    def render(state: GameState): ZIO[Console, Nothing, Unit] 
    for {
      a <- streamOfState.tap(s => render(s)).either.fold(ZIO.succeed(()))((_,_) => ZIO.succeed(())).exitCode
    } yield a
  }
}

//when I update GameState, I need random numbers, or I need to pull from a stream of random numbers that's initialized when I initialize GameState. Whats the best way to do this?
//
//I could turn streamOfState to be of type: ZStream[Console with Clock, IOException, ZIO[Random, Nothing, GameState]]
//but then how do I tap the stream and pass just GameState to render?
//
//I tried this:
  val foodStream: ZStream[Random, Nothing, Food] = 
    ZStream.repeatEffect {
      for {
        x <- nextIntBounded(width)
        y <- nextIntBounded(height)
      } yield Food(Position(x,y))
    } 


ZStream[Clock with Random, Nothing, Event] = ZStream.tick(500.millis).flatMap(_ => foodStream)

// but it seemed to disregard the 500.millis tick and just produce the Food without any regard for a time step
// I changed it to this out of curiousity:
foodStream.flatMap(f => ZStream.tick(500.millis).map(_ => f))
and this preserved the time step but the f became constant, instead of producing a new f at each tick 


*/ 

