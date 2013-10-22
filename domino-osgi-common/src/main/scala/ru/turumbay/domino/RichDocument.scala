package ru.turumbay.domino

import lotus.domino.{DateTime, Document}
import ru.turumbay.domino.RichDocument.FieldValueMapper
import java.util.{Vector => JavaVector, Date}
import scala.collection.JavaConversions._

class RichDocument(doc: Document) {
  val field: Fields = new Fields

  class Fields{
    def apply[T](name:String)(implicit mapper:FieldValueMapper[T]):List[T] = {
      mapper.read(doc, name)
    }

    def update[T](name:String, value:List[T])(implicit mapper:FieldValueMapper[T]) {
      mapper.write(doc,name,value)
    }
  }
}

object RichDocument {
  trait FieldValueMapper[T]{
    def read(doc:Document, field:String):List[T]
    def write(doc:Document, field:String, value: List[T]):Unit
  }

  def apply(doc:Document) = new RichDocument(doc)

  object implicits{
    sealed trait DefaultMapper[T] extends FieldValueMapper[T]{
      def write(doc: Document, field: String, value: List[T]) {
        doc.replaceItemValue(field, (new JavaVector[T]() /: value)( (v,el) => {v.add(el);v}))
      }

      def read(doc: Document, field: String) = doc.getItemValue(field).asInstanceOf[JavaVector[T]].toList
    }

    implicit object StringMapper extends DefaultMapper[String]

    implicit object DoubleMapper extends DefaultMapper[Double]

    implicit object DateTimeMapper extends DefaultMapper[DateTime]

    implicit object IntMapper extends DefaultMapper[Int]{
      override def read(doc: Document, field: String) = DoubleMapper.read(doc,field).map( _.toInt )
    }

    implicit object DateMapper extends FieldValueMapper[Date]{
      def read(doc: Document, field: String) = DateTimeMapper.read(doc,field).map( _.toJavaDate )

      def write(doc: Document, field: String, value: List[Date]) {
        val session = doc.getParentDatabase.getParent
        DateTimeMapper.write(doc, field, value.map( session createDateTime  ))
      }
    }
  }
}

