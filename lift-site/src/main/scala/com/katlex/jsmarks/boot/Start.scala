package com.katlex.jsmarks
package boot

import org.mortbay.jetty
import org.mortbay.jetty.webapp.WebAppContext
import xsbti.{AppMain, AppConfiguration}


object Server {
  def run(args:Array[String]): Int = {
    val server = new jetty.Server(8081)
    val context = new WebAppContext
    context.setServer(server)
    context.setContextPath("/")
    println(getClass.getResource("/webapp").toString)
    0
    /*context.setWar(getClass.getResource("/webapp").toString)

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
        System.exit(1)
    }*/
  }
}

/** The launched conscript entry point */
class Start extends AppMain {
  def run(config: xsbti.AppConfiguration) =
    Exit(Server.run(config.arguments))
}
case class Exit(val code: Int) extends xsbti.Exit