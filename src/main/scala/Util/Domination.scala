package Util

object Domination {

  def isDominated(left: Array[Double], right: Array[Double]): Boolean = {
    var same = true
    for (i <- left.indices) {
      if (left(i) < right(i))
        return false
      else if (same && left(i) > right(i))
        same = false
    }
    !same
  }

}
