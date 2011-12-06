package com.katlex.jsmarks
package boot

import bootstrap.liftweb.Boot
import model.JsMark
import java.io.File
import io.Source
import net.liftweb.common.{Full, LazyLoggable}

/**
 * Imports data to DB using predefined conventional folder structure
 */
object Import extends LazyLoggable with EntryPoint {
  def run(args:Array[String]) = {
    new Boot().boot
    val argsMap = args.zipWithIndex.map(e => e._2 -> e._1).toMap

    val sourceDir = new File("db/marks")
    var filesMap = Map.empty[String, Set[String]]

    sourceDir.listFiles().map { f =>
      f.getName.split("\\.").toList match {
        case name :: ext :: Nil =>
          filesMap += name -> filesMap.get(name).map(_ + ext).getOrElse(Set(ext))
        case _ =>
      }
    }

    for {
      (name, exts) <- filesMap
    } {
      val remainedExts = Set("meta", "js", "desc") -- exts
      if (!remainedExts.isEmpty) {
        logger.error("Bad configuraion. Following files should exist: %s" format (remainedExts.map(ext => name + "." + ext).mkString(", ")))
      } else {

        def fillRecord(record: JsMark) = {
          for (ext <- exts) {

            val file = new File(Seq(sourceDir.getAbsolutePath, name).mkString("/") + "." + ext)
            val src = Source.fromFile(file)

            ext match {
              case "js" => record.content(src.mkString)
              case "desc" => record.description(src.mkString)
              case "meta" => src.getLines().filter(_.trim.size != 0).toList match {
                case title :: Nil =>
                  JsMark.find("name", title)
                  record.name(title)
                case title :: params =>
                  record.name(title)
                  val paramsWithNulls = for {
                    paramLine <- params
                  } yield {
                    paramLine.split(" ").toList match {
                      case name :: description =>
                        JsMark.Param(name, description.mkString(" "))
                      case _ =>
                        logger.error("Bad param spec in file %s: %s" format (file.getName, paramLine))
                        null
                    }
                  }
                  record.params(paramsWithNulls.filterNot(_ == null))
                case _ =>
                  logger.error("Bad meta file: %s" format file.getName)
              }
              case _ =>
                logger.warn("Unknown config file: " + file)
            }
          }
          record
        }

        for {
          record <- Full(fillRecord(JsMark.createRecord))
          (realRecord, updated) <- JsMark.find("name", record.name.is).map { foundRecord =>
              (fillRecord(foundRecord), true)
            } .or(Full((record, false)))
          updatedString = if (updated) "Updated" else "Created"
        } {
          realRecord.save
          logger.info("%s record from file set: %s" format (updatedString, name))
        }
      }
    }

    0
  }
}