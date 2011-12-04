package com.katlex.jsmarks
package boot

import org.mortbay.jetty
import org.mortbay.jetty.webapp.WebAppContext
import xsbti.{AppMain, AppConfiguration}
import java.util.jar.JarFile
import java.io.{FileOutputStream, File}

object Server {
  val INSTALL_DIR = new File(System.getProperty("user.home") + File.separator + ".jsmarks")

  def installWebapp: Either[(String, Int), String] = {
    val webappRes = getClass.getResource("/webapp").toString
    if (webappRes == null) Left("Webapp resource not found on classpath", 1)
    else if (!webappRes.startsWith("jar:")) Left("Can't run from not jar classpath", 2)
    else {
      println(webappRes.split("!")(0))
      val jarFile = new JarFile(webappRes.split("!")(0).substring(9))
      val entries = jarFile.entries()

      if (INSTALL_DIR.exists()) {
        def deleteRecursive(file:File) {
          if (file.isDirectory) {
            for {
              child <- file.listFiles()
            } deleteRecursive(child)
          }
          file.delete();
        }

        deleteRecursive(INSTALL_DIR)
      }
      INSTALL_DIR.mkdir()

      while (entries.hasMoreElements) {
        val entry = entries.nextElement()
        val name = entry.getName

        if (name.startsWith("webapp") && !entry.isDirectory) {
          val buffer = Array.ofDim[Byte](4092)
          val input = jarFile.getInputStream(entry)
          val outputFile = new File(INSTALL_DIR, entry.getName.replace("webapp/", ""))
          outputFile.getParentFile.mkdirs()
          val output = new FileOutputStream(outputFile)

          def copyOnce():Boolean = {
            val read = input.read(buffer)
            if (read == -1) false else {
              output.write(buffer, 0, read)
              true
            }
          }

          while (copyOnce) { /* copy */ }

          input.close()
          output.close()
        }
      }
      
      Right(INSTALL_DIR.getAbsolutePath)
    }
  }

  def defaultSystemProperty(name:String, default:String) {
    import System._
    val value = getProperty(name)
    if (value == null) {
      setProperty(name, default)
    }
  }

  def run(args:Array[String]) = {
    installWebapp match {
      case Left((error, code)) =>
        println(error)
        code
      case Right(webappRoot) =>
        defaultSystemProperty("run.mode", "production")

        val server = new jetty.Server(8081)
        val context = new WebAppContext
        context.setServer(server)
        context.setContextPath("/")
        context.setWar(webappRoot)

        server.addHandler(context)

        try {
          server.start()
          while (System.in.available() == 0) {
            Thread.sleep(5000)
          }
          server.stop()
          server.join()
        } catch {
          case e:Exception =>
            e.printStackTrace()
            System.exit(3)
        }

        0 // Success error code
    }
  }
}

/** The launched conscript entry point */
class Start extends AppMain {
  def run(config: AppConfiguration) =
    Exit(Server.run(config.arguments))
}
case class Exit(val code: Int) extends xsbti.Exit