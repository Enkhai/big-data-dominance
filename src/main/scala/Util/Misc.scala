package Util

import scala.collection.mutable.ArrayBuffer

object Misc {

  def sortDominationPartition(partition: Iterator[(Array[Double], Int)]): Iterator[(Array[Double], Int)] = {
    partition.toList.sortBy(_._2).reverse.toIterator
  }

  def filterDominationPartition(partition: Iterator[(Array[Double], Int)], k: Int = 20): Iterator[Array[Double]] = {
    partition.toList.zipWithIndex.filter(_._2 < k).map(_._1._1).toIterator
  }

  def getMinValues(partition: Iterator[Array[Double]]): Iterator[Array[Double]] = {
    val partitionList = partition.toArray.transpose
    val minValues = ArrayBuffer[Double]()
    for (i <- partitionList.indices)
      minValues += partitionList(i).min
    Iterator(minValues.toArray)
  }

  def fillEmpty(partition: Iterator[Array[Double]], dim: Int): Iterator[Array[Double]] = {
    if (partition.isEmpty)
      return Iterator(Array.fill(dim)(Double.PositiveInfinity))
    partition
  }

}
