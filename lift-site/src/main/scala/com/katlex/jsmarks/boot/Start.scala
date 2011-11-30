package com.katlex.jsmarks.boot

import org.mortbay.jetty.Server
import org.mortbay.jetty.webapp.WebAppContext


object Start {
  def main(args:Array[String]) {
    val server = new Server(8081)
    val context = new WebAppContext()
    context.setServer(server)
    context.setContextPath("/")
    println(getClass.getResource("/webapp").toString)
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