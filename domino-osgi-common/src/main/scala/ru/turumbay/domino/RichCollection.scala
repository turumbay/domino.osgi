package ru.turumbay.domino

import lotus.domino.{ViewEntry, Document, ViewNavigator}
import scala.annotation.tailrec
import ru.turumbay.domino.Domino.Recyclable

class RichCollection[C <: Recyclable,X <: Recyclable](xs: C, nextA: (C, X) => X, firstA: C => X, filter: X => Boolean = (x: X) => true) {
  def map[T](f: X => T): List[T] = {
    @tailrec
    def iteration(docOption: Option[X], acc: List[T]): List[T] = docOption match {
      case None => acc
      case Some(doc) => {
        val (next: Option[X], result: List[T]) = Domino.using(doc) {
          doc =>

          /**
           * f should be applied before retrieving next document, otherwise, if
           * f throws exception - next would not be recycled
           */
            (
              if (filter(doc)) f(doc) :: acc else acc, // new acc
              Option(nextA(xs, doc)) // new next
              ).swap
        }
        iteration(next, result)
      }
    }
    iteration(Option(firstA(xs)), Nil)
  }

  def flatMap[T](f: X => T) = map(f)

  def withFilter(p: X => Boolean): RichCollection[C, X] = {
    new RichCollection(xs, nextA, firstA, (x: X) => filter(x) && p(x))
  }
}

object RichCollection {
  type WithFirstAndNextDocument = {
    def getNextDocument(doc: Document): Document
    def getFirstDocument(): Document
    def recycle(): Unit
  }

  def apply[B <: WithFirstAndNextDocument](xs: B) = new RichCollection[B, Document](xs, _.getNextDocument(_), _.getFirstDocument())

  def apply(nav: ViewNavigator) = new RichCollection[ViewNavigator, ViewEntry](nav, _.getNext(_), _.getFirst)
}




