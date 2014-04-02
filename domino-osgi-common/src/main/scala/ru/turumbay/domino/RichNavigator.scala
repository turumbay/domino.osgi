package ru.turumbay.domino

import lotus.domino.{ViewEntry, ViewNavigator, Document, View}
import scala.annotation.tailrec

class RichNavigator(nav:ViewNavigator){
  def map[T](f: ViewEntry => T): List[T] = {
    @tailrec
    def iteration(entry: Option[ViewEntry], acc: List[T]): List[T] = entry match {
      case None => acc
      case Some(nd) => {
        val (next: Option[ViewEntry], result: T) = Domino.using(nd) {
          /**
           * f should be applied before retrieving next document, otherwise, if
           * f throws exception - next would not be recycled
           */
          entry => (f(entry), Option(nav.getNext(entry))).swap
        }
        iteration(next, result :: acc)
      }
    }
    iteration(Option(nav.getFirst), Nil)
  }

  def flatMap[T](f: ViewEntry => T) = map(f)
}

object RichNavigator{
  def apply(nav:ViewNavigator) = new RichNavigator(nav:ViewNavigator)
}
