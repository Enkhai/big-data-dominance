import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j._

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

    val skylineHelper = new SkylineHelper()

    val points = sc.textFile(inputFile)
      .map(x => x.split(","))
      .map(x => x.map(y => y.toDouble))

    val result = sc.makeRDD(
      skylineHelper.skyline(points.toLocalIterator)
    ).map(x => x.mkString(", "))
    result.saveAsTextFile(outputDir)

    sc.stop()
  }
}