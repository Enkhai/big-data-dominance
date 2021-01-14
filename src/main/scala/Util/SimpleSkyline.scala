package Util

import Util.Domination.isDominated

object SimpleSkyline {

  def calculate(x: Iterator[Array[Double]]): Iterator[Array[Double]] = {
    var tempList = x.toList
    var i = 0
    var listLength = tempList.length
    while (i < listLength - 1) {
      var k = i + 1
      while (k < listLength) {
        if (isDominated(tempList(i), tempList(k))) {
          tempList = tempList.take(k) ++ tempList.drop(k + 1)
          k -= 1
          listLength -= 1
        } else if (isDominated(tempList(k), tempList(i))) {
          tempList = tempList.take(i) ++ tempList.drop(i + 1)
          listLength -= 1
          i -= 1
          k = listLength
        }
        k += 1
      }
      i += 1
    }
    tempList.toIterator
  }

}
