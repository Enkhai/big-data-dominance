package Util

import Util.Domination.isDominated

object SimpleSkyline {

  def calculate(partition: Iterator[Array[Double]]): Iterator[Array[Double]] = {
    var partitionList = partition.toList
    var i = 0
    var listLength = partitionList.length
    while (i < listLength - 1) {
      var k = i + 1
      while (k < listLength) {
        if (isDominated(partitionList(i), partitionList(k))) {
          partitionList = partitionList.take(k) ++ partitionList.drop(k + 1)
          k -= 1
          listLength -= 1
        } else if (isDominated(partitionList(k), partitionList(i))) {
          partitionList = partitionList.take(i) ++ partitionList.drop(i + 1)
          listLength -= 1
          i -= 1
          k = listLength
        }
        k += 1
      }
      i += 1
    }
    partitionList.toIterator
  }

}
