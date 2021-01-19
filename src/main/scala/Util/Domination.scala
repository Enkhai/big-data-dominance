package Util

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}

object Domination {

  def isDominated(left: Array[Double], right: Array[Double]): Boolean = {
    // left dominates right
    var same = true
    for (i <- left.indices) {
      if (left(i) > right(i))
        return false
      else if (same && left(i) < right(i))
        same = false
    }
    !same
  }

  def calculateDomination(partition: Iterator[Array[Double]]): Iterator[(Array[Double], Int)] = {
    var buffer = ArrayBuffer[(Array[Double], Int)]()
    val partitionList = partition.toList
    for (i <- partitionList.indices) {
      var domScore = 0
      for (j <- partitionList.indices) {
        breakable {
          if (i == j)
            break()
          if (isDominated(partitionList(i), partitionList(j)))
            domScore += 1
        }
      }
      buffer += Tuple2(partitionList(i), domScore)
    }
    buffer.toIterator
  }

}
