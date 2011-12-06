package com.katlex.jsmarks
package utils

import net.liftweb.common.{Box, Full, Empty, Failure}

object BoxUtils {
  implicit def booleanToBox(b: Boolean) = if (b) Full(()) else Empty
  def tryo[T](f: => T):Box[T] = try { Full(f) } catch { case e => Failure(e.getMessage, Full(e), Empty) }
}