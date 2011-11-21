package com.katlex.jsmarks
package model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.record.field.{IntField, StringField}
import net.liftweb.mongodb.{JsonObjectMeta, JsonObject}
import net.liftweb.mongodb.record.field.{MongoCaseClassListField, MongoListField, ObjectIdPk}
import net.liftweb.common.Full
import net.liftweb.sitemap.Menu


class JsMark private() extends MongoRecord[JsMark] with ObjectIdPk[JsMark] {
  import JsMark._

  def meta = JsMark

  object name extends StringField(this, "Alert")
  object params extends MongoCaseClassListField[JsMark, Param](this) {
    override def defaultValueBox = Full(Param("message", "Message which will be shown in alert") :: Nil)
  }
  object content extends StringField(this, "window.alert(message)")
}

object JsMark extends JsMark with MongoMetaRecord[JsMark] {
  object ParamType extends Enumeration {
    val String = Value("String")
  }
  case class Param(name:String, description:String)

}


