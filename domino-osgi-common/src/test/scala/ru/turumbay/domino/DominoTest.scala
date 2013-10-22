package ru.turumbay.domino

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import Domino.implicits._

@RunWith(classOf[JUnitRunner])
class DominoTest extends FunSuite{

  test("domino objects could be used in for comprehension and it should compiles"){
    for{
      session <- Domino.Session
      db  <- session.getDatabase("","names.nsf")
      doc <- db.getAllDocuments
      dt  <- doc.getLastModified
    }yield (db.getReplicaID, doc.getUniversalID)
  }
}
