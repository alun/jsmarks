package com.katlex.jsmarks
package utils

import net.liftweb.common.{Full, Empty}

object BoxUtils {
  implicit def booleanToBox(b: Boolean) = if (b) Full(()) else Empty
}