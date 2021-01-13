class SkylineHelper extends Serializable {

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

  def skyline(x: Iterator[Array[Double]]): List[Array[Double]] = {
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
    tempList
  }

}
