package Util

import Util.Domination.isDominated

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}

object SFSSkyline {

  def calculate(x: Array[Array[Double]]): Iterator[Array[Double]] = {
    val buffer = ArrayBuffer[Array[Double]]()
    buffer += x(0)
    for (i <- 1 until x.length) {
      var j = 0
      var toBeAdded = true
      breakable {
        while (j < buffer.length) {
          if (isDominated(x(i), buffer(j))) {
            buffer.remove(j)
            j -= 1
          } else if (isDominated(buffer(j), x(i))) {
            toBeAdded = false
            break()
          }
          j += 1
        }
      }
      if (toBeAdded)
        buffer += x(i)
    }
    buffer.toIterator
  }

  def addScoreAndCalculate(array: Iterator[Array[Double]]): Iterator[Array[Double]] = {
    calculate(
      array.map(x => (x, 0))
        .map(x => {
          var sum = 0.0
          for (i <- x._1.indices)
            sum += math.log(x._1(i) + 1)
          (x._1, sum)
        }).toArray
        .sortBy(x => -x._2)
        .map(x => x._1)
    )
  }

}
