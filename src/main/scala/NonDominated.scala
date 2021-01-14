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
    val inputFile = "file://" + currentDir + "/datasets/gaussian_size1000_dim5.csv"
    val outputDir = "file://" + currentDir + "/output"

    val points = sc.textFile(inputFile)
      .map(x => x.split(","))
      .map(x => x.map(y => y.toDouble))

    val result = points.mapPartitions(addScoreAndCalculate) // calculate the skyline for each partition
      .coalesce(1) // reduce to 1 partition
      .mapPartitions(addScoreAndCalculate) // and calculate the skyline again for all points

    result.map(x => x.mkString(", ")).saveAsTextFile(outputDir)
    sc.stop()
  }
}