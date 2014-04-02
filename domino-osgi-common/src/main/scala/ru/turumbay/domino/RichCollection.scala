package ru.turumbay.domino

import lotus.domino.{Document, DocumentCollection}
import scala.annotation.tailrec


class RichCollection[A <: { def getNextDocument(doc:Document):Document
                            def getFirstDocument():Document }](collection:A){
  def map[T](f: Document => T): List[T] = {
    @tailrec
    def iteration(doc: Option[Document], acc: List[T]): List[T] = doc match {
      case None => acc
      case Some(nd) => {
        val (next: Option[Document], result: T) = Domino.using(nd) {
          /**
           * f should be applied before retrieving next document, otherwise, if
           * f throws exception - next would not be recycled
           */
          doc => (f(doc), Option(collection.getNextDocument(doc))).swap
        }
        iteration(next, result :: acc)
      }
    }
    iteration(Option(collection.getFirstDocument), Nil)
  }

  def flatMap[T](f: Document => T) = map(f)
}

object RichCollection{
  def apply[A  <: { def getNextDocument(doc:Document):Document
    def getFirstDocument():Document }](collection:A) = new RichCollection[A](collection)
}