package com.katlex.jsmarks
package boot

import xsbti.{AppConfiguration, AppMain}

/**
 * JS Marks applications launched with conscript
 */
package Launch {
  class Server extends LauncherAdapter(boot.Server)
  class Import extends LauncherAdapter(boot.Import)
}

trait EntryPoint {
  def run(args:Array[String]): Int
  def main(args:Array[String]) { System.exit(run(args)) }
}

case class LauncherAdapter(ep:EntryPoint) extends AppMain {
  case class Exit(val code: Int) extends xsbti.Exit
  def run(config: AppConfiguration) = Exit(ep.run(config.arguments))
}