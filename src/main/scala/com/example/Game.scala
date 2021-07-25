package com.example.game

import cats.effect.{ContextShift, IO, Timer}
//import com.github.lpld.games.tetris.Event._
import fs2.{Pure, Stream}

import scala.concurrent.duration.DurationLong

import com.example.game.Event._

sealed trait Event
object Event {
  case object Tick extends Event
  case class UserAction(char: Char) extends Event
}

sealed case class GameState (char: Char)

object Game {

  val downInterval = 2
  val linesPerLevel = 10
}

class Game(height: Int, width: Int, interactions: Stream[IO, Char])
(implicit timer: Timer[IO], contextShift: ContextShift[IO]) {


  /**
   * This stream reflects all changes in the state of game field.
   */
  val gameStates: Stream[IO, GameState] = {

    // Two sources of events:
    // 1. Regular ticks
    val tick: Stream[IO, Event] = Stream.fixedRate[IO](500.millis).map(_ => Tick)
    // 2. User's interactions
    val userMoves: Stream[IO, Event] = interactions.map(UserAction)
    // merge them
    val allEvents: Stream[IO, Event] = tick merge userMoves

    val initial = GameState('a')

    val states = allEvents.scan(initial)(nextState)
    states.takeWhile(_.char != 'b')
  }

  /**
   * Compute next game state given the previous state and an event.
   */
  private def nextState(state: GameState, event: Event): GameState =
    event match {
      case Tick => state

      case UserAction(char) => GameState(char)
    }

}
