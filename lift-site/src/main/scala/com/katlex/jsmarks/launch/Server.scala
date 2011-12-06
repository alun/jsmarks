package com.katlex.jsmarks
package boot

import utils.BoxUtils
import org.mortbay.jetty
import org.mortbay.jetty.webapp.WebAppContext
import java.io.{FileOutputStream, File}
import java.util.zip.ZipEntry
import net.liftweb.common.{Failure, Full, Box}
import java.util.jar.{JarEntry, JarFile}

/**
 * Start embedded Jetty server with JSMarks application deployed in root
 */
object Server extends EntryPoint {
  import BoxUtils._
  
  case class JarFileOps(jarFile:JarFile) {
    def unzipEntry(entry:ZipEntry, to:File) = tryo {
      val buffer = Array.ofDim[Byte](4092)
      val input = jarFile.getInputStream(entry)
      to.getParentFile.mkdirs()
      val output = new FileOutputStream(to)

      def copyNextBlock: Unit = {
        val read = input.read(buffer)
        if (read != -1) {
          output.write(buffer, 0, read)
          copyNextBlock
        }
      }
      copyNextBlock

      input.close()
      output.close()
    }
  }

  val INSTALL_DIR = new File(System.getProperty("user.home") + File.separator + ".jsmarks")

  def run(args:Array[String]) = installWebapp match {
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
        println("Starting server... Press any key to stop")
        server.start()
        while (System.in.available() == 0) {          
          Thread.sleep(5000)
        }
        println("Key preseed. Stopping...")
        server.stop()
        server.join()

        0
      } catch {
        case e:Exception =>
          e.printStackTrace()
          3
      }
  }

  /**
   * Extracts webapp from jar to temporary predefined folder
   */
  def installWebapp: Either[(String, Int), String] = {
    val webappRes = getClass.getResource("/webapp").toString
    if (webappRes == null) Left("Webapp resource not found on classpath", 1)
    else if (!webappRes.startsWith("jar:")) Left("Can't run from not jar classpath", 2)
    else unzipWebApp(webappRes.split("!")(0)) match {
      case Full(_) => Right(INSTALL_DIR.getPath)
      case Failure(_, Full(e), _) => e.printStackTrace(); Left(("Webapp extraction error", 3))
      case _ => Left(("Webapp extraction error", 3))
    }
  }

  def unzipWebApp(jarPath:String) = {
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
    INSTALL_DIR.mkdirs()

    val jarFile = new JarFile(jarPath.substring(9))
    val entries = jarFile.entries()

    /*
     * Filter extractor of webapp file jar entry and target file (where to install)
     */
    val WebAppFile = new {
      def unapply(entry:ZipEntry) = {
        val name = entry.getName
        if (!entry.isDirectory && name.startsWith("webapp"))
          Some((entry, new File(INSTALL_DIR, name.replace("webapp/", ""))))
        else None
      }
    }

    val webappEntries = for {
      WebAppFile(entry, target) <- new Iterator[JarEntry] {
        def hasNext = entries.hasMoreElements
        def next() = entries.nextElement
      }
    } yield (entry, target)

    webappEntries.foldLeft[Box[Unit]](Full(())) {
      case (prevBox, (entry, target)) =>
        prevBox.flatMap(_ => jarFile.unzipEntry(entry, target))
    }
  }

  implicit def addJarFileOps(jarFile:JarFile):JarFileOps = JarFileOps(jarFile)

  def defaultSystemProperty(name:String, default:String) {
    import System._
    val value = getProperty(name)
    if (value == null) {
      setProperty(name, default)
    }
  }
}