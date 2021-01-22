import Util.AngularPartitioner
import Util.Misc.{fillEmpty, getMinValues}
import Util.SFSSkyline.addScoreAndCalculate
import org.apache.log4j._
import org.apache.spark.{SparkConf, SparkContext}

object NonDominated {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org.apache.spark.SparkContext").setLevel(Level.WARN)

    val sparkConf = new SparkConf()
      .setMaster("local[4]")
      .setAppName("NonDominated")

    val sc = new SparkContext(sparkConf)

    val currentDir = System.getProperty("user.dir")
    val inputFile = "file://" + currentDir + "/datasets/gaussian_size1000000_dim5.csv"
    val outputDir = "file://" + currentDir + "/output"

    var points = sc.textFile(inputFile)
      .map(x => x.split(","))
      .map(x => x.map(y => y.toDouble))

    // calculate min dimension values for angular partitioning
    val minVal = points.mapPartitions(getMinValues)
      .coalesce(1)
      .mapPartitions(getMinValues)
      .collect
      .head
    // number of partitions should be multiple of 3
    val numPartitions = 6
    val partitioner = new AngularPartitioner(numPartitions, minVal)

    points = points
      .mapPartitions(_.toList.map(x => (partitioner.makeKey(x), x)).toIterator)
      .partitionBy(partitioner)
      .mapPartitions(_.toList.map(_._2).toIterator)
      // fill in empty partitions with dummy data to avoid ArrayOutOfIndex error
      .mapPartitions(partition => fillEmpty(partition, minVal.length))

    val result = points.mapPartitions(addScoreAndCalculate) // calculate the skyline for each partition
      .coalesce(1) // reduce to 1 partition
      .mapPartitions(addScoreAndCalculate) // and calculate the skyline again for all points

    result.map(x => x.mkString(", ")).saveAsTextFile(outputDir)
    sc.stop()
  }
}