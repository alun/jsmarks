package com.katlex.jsmarks
package lib

import net.liftweb.common.LazyLoggable
import com.yahoo.platform.yui.compressor.JavaScriptCompressor
import java.lang.String
import org.mozilla.javascript.{EvaluatorException, ErrorReporter}
import java.io.{StringWriter, StringReader, ByteArrayInputStream}


object  Compressor {

  private class Reporter extends ErrorReporter with LazyLoggable {
    def warning(message: String, sourceName: String, line: Int, lineSource: String, lineOffset: Int) {
      if (line < 0) {
          logger.warn(message)
      } else {
          logger.warn(line + ':' + lineOffset + ':' + message)
      }
    }

    def error(message: String, sourceName: String, line: Int, lineSource: String, lineOffset: Int) {
      if (line < 0) {
          logger.error(message)
      } else {
          logger.error(line + ':' + lineOffset + ':' + message)
      }
    }

    def runtimeError(message: String, sourceName: String, line: Int, lineSource: String, lineOffset: Int) = {
      error(message, sourceName, line, lineSource, lineOffset);
      new EvaluatorException(message);
    }
  }

  def compress(js:String) = {
    val reader = new StringReader(js)
    val writer = new StringWriter
    val compressor = new JavaScriptCompressor(reader, new Reporter)
    reader.close()
    compressor.compress(writer, -1, true, false, false, false)
    writer.toString
  }

}