import Util.Domination.calculateDomination
import org.apache.log4j._
import org.apache.spark.{SparkConf, SparkContext}

object HighestDominance {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org.apache.spark.SparkContext").setLevel(Level.WARN)

    val sparkConf = new SparkConf()
      .setMaster("local[4]")
      .setAppName("HighestDominance")

    val sc = new SparkContext(sparkConf)

    val currentDir = System.getProperty("user.dir")
    val inputFile = "file://" + currentDir + "/datasets/gaussian_size1000_dim2.csv"
    val outputDir = "file://" + currentDir + "/output"

    val points = sc.textFile(inputFile)
      .map(x => x.split(","))
      .map(x => x.map(y => y.toDouble))

    val k = 20
    val result = points.mapPartitions(calculateDomination)
      .sortBy(_._2, ascending = false)
      .zipWithIndex
      .filter(_._2 < k)

    result.map(_._1._1.mkString(", ")).saveAsTextFile(outputDir)

    sc.stop()
  }
}