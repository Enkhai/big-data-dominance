package Util

object Misc {

  def sortDominationPartition(partition: Iterator[(Array[Double], Int)]): Iterator[(Array[Double], Int)] = {
    partition.toList.sortBy(_._2).reverse.toIterator
  }

  def filterDominationPartition(partition: Iterator[(Array[Double], Int)], k: Int = 20): Iterator[Array[Double]] = {
    partition.toList.zipWithIndex.filter(_._2 < k).map(_._1._1).toIterator
  }

}
