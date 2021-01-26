import Util.AngularPartitioner
import Util.Domination.calculateDomination
import Util.Misc.{applyMinValue, fillEmpty, filterDominationPartition, getMinValues}
import org.apache.log4j._
import org.apache.spark.{SparkConf, SparkContext}

object HighestDominance {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org.apache.spark.SparkContext").setLevel(Level.WARN)

    val sparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("HighestDominance")

    val sc = new SparkContext(sparkConf)

    val currentDir = System.getProperty("user.dir")
    val inputFile = "file://" + currentDir + "/datasets/uniform_size1000000_dim3.csv"
    val outputDir = "file://" + currentDir + "/output"

    var points = sc.textFile(inputFile)
      .map(x => x.split(","))
      .map(x => x.map(y => y.toDouble))

    val timeBefore = System.nanoTime

    val minVal = points.mapPartitions(getMinValues)
      .coalesce(1)
      .mapPartitions(getMinValues)
      .collect
      .head
    val numPartitions = 9
    val partitioner = new AngularPartitioner(numPartitions, minVal.length)

    points = points
      .mapPartitions(partition => applyMinValue(partition, minVal))
      .mapPartitions(_.map(x => (partitioner.makeKey(x), x)))
      .partitionBy(partitioner)
      .mapPartitions(_.map(_._2))
      .mapPartitions(partition => fillEmpty(partition, minVal.length))

    val result = points.mapPartitions(calculateDomination)
      .mapPartitions(partition => filterDominationPartition(partition))
      .repartition(1)
      .mapPartitions(calculateDomination)
      .mapPartitions(partition => filterDominationPartition(partition))
      .mapPartitions(partition => applyMinValue(partition, minVal, subtract = false))

    result.map(_.mkString(", ")).saveAsTextFile(outputDir)

    print("######### Time taken for top 20 calculation #########")
    print(System.nanoTime - timeBefore / 1e9d)

    sc.stop()
  }
}