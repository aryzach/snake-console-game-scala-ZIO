package com.example.game

import cats.effect.{ContextShift, IO, Timer}
import fs2.{Pure, Stream}

import scala.concurrent.duration.DurationLong

import com.example.game.Event._
import com.example.game.Direction._

sealed trait Event
object Event {
  case object Tick extends Event
  case class UserAction(key: Direction) extends Event {
    def stringify: String = key match {
      case Up => "up"
      case Down => "down"
      case Left => "left"
      case Right => "right"
    }
  }
}


sealed trait Direction
object Direction {
  object Up extends Direction 
  object Down extends Direction
  object Left extends Direction 
  object Right extends Direction 
}

sealed case class Position(x: Int, y: Int) {
  def up: Position = Position(x - 1, y)
  def down: Position = Position(x + 1, y)
  def left: Position = Position(x, y - 1)
  def right: Position = Position(x, y + 1)
}

sealed case class Parts(p: List[Position]) {
  def update(d: Direction) = 
    d match {
      case Direction.Up => Parts(List(p.head.up))
      case Direction.Down => Parts(List(p.head.down))
      case Direction.Left => Parts(List(p.head.left))
      case Direction.Right => Parts(List(p.head.right))
    }

  def contains(v: Position): Boolean = p.contains(v)
}


sealed case class GameState (parts: Parts, direction: Direction, event: UserAction)

object Game {

  val downInterval = 2
  val linesPerLevel = 10
}

class Game(height: Int, width: Int, interactions: Stream[IO, Direction])
(implicit timer: Timer[IO], contextShift: ContextShift[IO]) {

  val initialParts = Parts(List(Position(3,4)))
  val initialState = GameState(initialParts, Direction.Right, UserAction(Right))

  /**
   * This stream reflects all changes in the state of game field.
   */
  val gameStates: Stream[IO, GameState] = {

    // Two sources of events:
    // 1. Regular ticks
    val tick: Stream[IO, Event] = Stream.fixedRate[IO](250.millis).map(_ => Tick)
    // 2. User's interactions
    val userMoves: Stream[IO, Event] = interactions.map(UserAction)
    // merge them
    val allEvents: Stream[IO, Event] = tick merge userMoves


    val states = allEvents.scan(initialState)(nextState)
    states.takeWhile(_.direction != false)
  }

  /**
   * Compute next game state given the previous state and an event.
   */
  private def nextState(state: GameState, event: Event): GameState = {
    event match {
      case Tick => GameState(state.parts.update(state.direction), state.direction, state.event)
      case UserAction(direction) => GameState(state.parts, direction, UserAction(direction))
    }
  }

}
