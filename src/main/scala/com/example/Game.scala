package com.example.game

import zio._
import zio.stream.ZStream
import zio.stream._
import zio.console._
import zio.clock._
import zio.duration._
import zio.random._
import java.io.IOException

import scala.collection.immutable.Queue

import com.example.game.Event._
import com.example.utils.TypeAlias._

sealed trait Event
object Event {
  case class UserAction(key: Position) extends Event 
  case class Food(p: Position) extends Event
}

sealed  case class Position(x: Int, y: Int) {
  val stringify = x.toString + " " + y.toString
  def up: Position = Position(x - 1, y)
  def down: Position = Position(x + 1, y)
  def left: Position = Position(x, y - 1)
  def right: Position = Position(x, y + 1)
  def wrap(w: Int)(h: Int) = {
    if (x > w) {
      Position(0, y)
    } else if (x < 0) {
      Position(w, y)
    } else if (y > h) {
      Position(x, 0)
    } else if (y < 0) {
      Position(x, h)
    } else {
      this
    }
  }
  def add(p: Position) = Position(x + p.x, y + p.y)
  def equals(p: Position) = (x == p.x) && (y == p.y)
}


sealed case class GameState (parts: Parts, direction: Position, width: Int, height: Int, food: Position) {
  def update(newFood: Position) = {
    val h = parts.last
    if (food.equals(h)) {
      this.copy(parts = parts.enqueue(h.add(direction)).map(p => p.wrap(width)(height)), food = newFood)
    } else {
      this.copy(parts = parts.enqueue(h.add(direction)).dequeue._2.map(p => p.wrap(width)(height)))
    }
  }


}

object Game {
//  val downInterval = 2
//  val linesPerLevel = 10
}

class Game(width: Int, height: Int, interactions: ZStream[Console, IOException, Position])  {

  val initialParts = Queue(Position(3,4))
  val initialState: GameState = GameState(initialParts, Position(0,0).left, width, height, Position(1,1))

  /**
   * This stream reflects all changes in the state of game field.
   */
  val gameStates: ZStream[Console with Clock with Random, IOException, GameState] = {

    // Two sources of events and "precomputed" random Food:
    // Random food
    // maybe a stream isn't the best approach? Is this more or less efficient than getting a random Food only when needed? If the stream is precomputed in Chunks, maybe it's more efficient?
    val foodStream: ZStream[Random, Nothing, Food] = 
      ZStream.repeatEffect {
        for {
          x <- nextIntBounded(width)
          y <- nextIntBounded(height)
        } yield Food(Position(x,y))
      } 
    // Regular ticks
    val tick: ZStream[Clock, Nothing, Unit] = ZStream.tick(125.millis)
    // Timed Food
    val tickedFood: ZStream[Clock with Random, Nothing, Food] = tick.zip(foodStream).map(x => x._2)
    // User's interactions
    val userMoves: ZStream[Console, IOException, UserAction] = interactions.map(x => UserAction(x))
    // merge them
    val allEvents: ZStream[Console with Clock with Random, IOException, Event] = tickedFood merge userMoves

    val states: ZStream[Console with Clock with Random, IOException, GameState] = allEvents.scan(initialState)(nextState)

    states.takeWhile(_.direction != false)
  }
  /**
   * Compute next game state given the previous state and an event.
   */
  private def nextState(state: GameState, event: Event): GameState = {
    event match {
      case Food(p) => state.update(p)
      case UserAction(direction) => state.copy(direction = direction)
    }
  }

}
