package ru.turumbay.domino

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._
import Domino.implicits._
import RichDocument.implicits._

import lotus.domino.Document


@RunWith(classOf[JUnitRunner])
class RichDocumentTest extends FunSuite {

  def withNewDoc[T](f:Document => T) = for{
    session <- Domino.Session
    db <- session.getDatabase("", "names.nsf")
    doc <- db.createDocument()
  } yield f(doc)

  test("Missing string field returns as empty List") {
    withNewDoc{
      _.field[String]("str") should be ('empty)
    }
  }

  test("Empty string field returns as empty List"){
    withNewDoc{ doc =>
      doc.replaceItemValue("str", "")
      doc.field[String]("str") should be ('empty)
    }
  }

  test("Empty string field could be retrieved as Empty List[Double]"){
    withNewDoc{ doc =>
      doc.replaceItemValue("str", "")
      doc.field[Double]("str") should be ('empty)
    }
  }

  test("Missing number field returns as empty List"){
    withNewDoc{ doc =>
      doc.replaceItemValue("str", "")
      doc.field[Double]("num") should be ('empty)
    }
  }
}
