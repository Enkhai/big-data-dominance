import org.apache.log4j._
import org.apache.spark.{SparkConf, SparkContext}
import Util.SFSSkyline.addScoreAndCalculate

object NonDominatedTopK {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org.apache.spark.SparkContext").setLevel(Level.WARN)

    val sparkConf = new SparkConf()
      .setMaster("local[4]")
      .setAppName("NonDominatedTopK")

    val sc = new SparkContext(sparkConf)

    val currentDir = System.getProperty("user.dir")
    val inputFile = "file://" + currentDir + "/datasets/gaussian_size1000_dim5.csv"
    val outputDir = "file://" + currentDir + "/output"

    val points = sc.textFile(inputFile)
      .map(x => x.split(","))
      .map(x => x.map(y => y.toDouble))

    // TODO
    val result = points
    result.saveAsTextFile(outputDir)

    sc.stop()
  }
}