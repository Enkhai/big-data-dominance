import Util.AngularPartitioner
import Util.Domination.calculateDomination
import Util.Misc.{applyMinValue, fillEmpty, filterDominationPartition, getMinValues}
import Util.SFSSkyline.addScoreAndCalculateWithDomination
import org.apache.log4j._
import org.apache.spark.{SparkConf, SparkContext}

object NonDominatedTopK {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org.apache.spark.SparkContext").setLevel(Level.WARN)

    val sparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("NonDominatedTopK")

    val sc = new SparkContext(sparkConf)

    val currentDir = System.getProperty("user.dir")
    val inputFile = "file://" + currentDir + "/datasets/uniform_size10000_dim3.csv"
    val outputDir = "file://" + currentDir + "/output"

    var points = sc.textFile(inputFile)
      .map(x => x.split(","))
      .map(x => x.map(y => y.toDouble))

    val minVal = points.mapPartitions(getMinValues)
      .repartition(1)
      .mapPartitions(getMinValues)
      .collect
      .head
    val numPartitions = 9
    val partitioner = new AngularPartitioner(numPartitions, minVal.length)

    val timeBefore = System.nanoTime

    points = points
      .mapPartitions(partition => applyMinValue(partition, minVal))
      .mapPartitions(_.map(x => (partitioner.makeKey(x), x)))
      .partitionBy(partitioner)
      .mapPartitions(_.map(_._2))
      .mapPartitions(partition => fillEmpty(partition, minVal.length))

    val result = points.mapPartitions(calculateDomination)
      .mapPartitions(addScoreAndCalculateWithDomination)
      .mapPartitions(partition => filterDominationPartition(partition))
      .repartition(1)
      .mapPartitions(calculateDomination)
      .mapPartitions(addScoreAndCalculateWithDomination)
      .mapPartitions(partition => filterDominationPartition(partition))
      .mapPartitions(partition => applyMinValue(partition, minVal, subtract = false))

    result.map(_.mkString(", ")).saveAsTextFile(outputDir)

    print("######### Time taken for top 20 skyline calculation #########\n")
    print((System.nanoTime - timeBefore) / 1e6d)

    sc.stop()
  }
}