package org.microsoft.news

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.microsoft.news.dataloader.NewsDataLoader
import org.microsoft.news.datawriter.DataFileWriterLocal
import org.microsoft.news.entity.News

object DataExtractEngineHelper {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder()
      .appName("DataExtractEngine")
      .master("local[*]")
      .getOrCreate()

    val sc = spark.sparkContext
    val initialDataPath: String = args(0)
    val transformDataPath: String = args(1)

    val newsDataLoader = new NewsDataLoader(s"$initialDataPath/news.tsv", spark)
    val newsRDD: RDD[News] = newsDataLoader.loadRDD()
    val newsDF = spark.createDataFrame(newsRDD)

    DataFileWriterLocal.dataWriter(dataFrame = newsDF,
      dataPath = transformDataPath,
      directoryName = "newsdata")

  }

}
