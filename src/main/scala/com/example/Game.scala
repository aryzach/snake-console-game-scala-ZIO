package com.example.game

import zio.stream.ZStream
import zio.console._
import zio.clock._
import zio.duration._
import java.io.IOException


import scala.concurrent.duration.DurationLong
import scala.collection.immutable.Queue

import com.example.game.Event._
import com.example.utils.TypeAlias._

sealed trait Event
object Event {
  case object Tick extends Event
  case class UserAction(key: Position) extends Event 
}

sealed case class Position(x: Int, y: Int) {
  val stringify = x.toString + " " + y.toString
  def up: Position = Position(x - 1, y)
  def down: Position = Position(x + 1, y)
  def left: Position = Position(x, y - 1)
  def right: Position = Position(x, y + 1)
  def update(d: Position)(w: Int)(h: Int) = {
    val temp = Position(x,y).add(d)
    if (temp.x > w) {
      Position(0, y)
    } else if (temp.x < 0) {
      Position(w, y)
    } else if (temp.y > h) {
      Position(x, 0)
    } else if (temp.y < 0) {
      Position(x, h)
    } else {
      temp
    }
  }
  def add(p: Position) = Position(x + p.x, y + p.y)
  def equals(p: Position) = (x == p.x) && (y == p.y)
}

/*
sealed case class Parts(p: Queue[Position]) {
  def advance(d: Position)(w: Int)(h: Int) = Parts(p.map(v => v.update(d)(w)(h)))
  def contains(v: Position): Boolean = p.contains(v)
  val head = p.head
  def push(d: Position) = Parts(d +: p)
}
*/


sealed case class GameState (parts: Parts, direction: Position, width: Int, height: Int, food: Position) {
  def update() = {
    val h = parts.last
    if (food.equals(h)) {
      this.copy(parts = parts.enqueue(h.add(direction)), food = Position(10,10))
    } else {
      this.copy(parts = parts.enqueue(h.add(direction)).dequeue._2)
    }
  }
}

object Game {
  val downInterval = 2
  val linesPerLevel = 10
}

class Game(width: Int, height: Int, interactions: ZStream[Console, IOException, Position])  {

  val initialParts = Queue(Position(3,4))
  val initialState = GameState(initialParts, Position(0,0).left, width, height, Position(1,1))

  /**
   * This stream reflects all changes in the state of game field.
   */
  val gameStates: ZStream[Console with Clock, IOException, GameState] = {

    // Two sources of events:
    // 1. Regular ticks
    val tick: ZStream[Clock, Nothing, Event] = ZStream.tick(125.millis).map(_ => Tick)
    // 2. User's interactions
    val userMoves: ZStream[Console, IOException, UserAction] = interactions.map(x => UserAction(x))
    // merge them
    val allEvents: ZStream[Console with Clock, IOException, Event] = tick merge userMoves

    val states = allEvents.scan(initialState)(nextState)
    states.takeWhile(_.direction != false)
  }

  /**
   * Compute next game state given the previous state and an event.
   */
  private def nextState(state: GameState, event: Event): GameState = {
    event match {
      case Tick => state.update()
      case UserAction(direction) => state.copy(direction = direction)
    }
  }
}
