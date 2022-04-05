package org.microsoft.news

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.util.parsing.json.JSON

object DataExtractEngineHelper {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder()
      .appName("DataExtractEngine")
      .master("local[*]")
      .getOrCreate()

    val l: List[Any] = List(Map("A" -> "OMEGA-TEST", "B" -> List('A', 'B')), "String Test")

    //l.foreach(list => println(list))

    /* l.foreach {
       value => {
         val printVal = value match {
           case map: Map[String, Any] => {
             map.map {
               case (key, mapVal) => {
                 println(key + " : " + mapVal)
               }
             }
           }
           case _ => println(value)
         }
       }
     }
 */

    val sc = spark.sparkContext
    val path: String = args(0)

    val dataPath: String = s"$path/news.tsv"

    val testRdd: RDD[String] = sc.textFile(dataPath)

    print(testRdd.count())

    testRdd
      .map(row => row.split("\t", -1))
      .foreach(
        str => {
          val titlesEntities = JSON.parseFull(str(str.length - 1)).toList

          titlesEntities.map {
            list => {
              val label = list.asInstanceOf[List[Map[String, Any]]].map(map => map("Label").toString)
              val wikiDataId = list.asInstanceOf[List[Map[String, Any]]].map(map => map("WikidataId").toString)
              val confidence = list.asInstanceOf[List[Map[String, Any]]].map(map => map("Confidence").toString)
              val titleType = list.asInstanceOf[List[Map[String, Any]]].map(map => map("Type").toString)
              val occurrenceOffsets = list.asInstanceOf[List[Map[String, Any]]].map(map => map("OccurrenceOffsets").toString)
              val surfaceForms = list.asInstanceOf[List[Map[String, Any]]].map(map => map("SurfaceForms").toString)

              val wikiDataIdString = wikiDataId
                .map(entity =>
                  entity.toString.replace("List(", "").replace(")", ""))
              val entitiesListString = "[" + wikiDataIdString.mkString(",") + "]"
              val labelString = "[" + label.mkString(",") + "]"
              val confidenceString = "[" + confidence.mkString(",") + "]"
              val titleTypeString = "[" + titleType.mkString(",") + "]"
              val occurrenceOffsetsString = "[" + occurrenceOffsets.mkString(",") + "]"
              val surfaceFormsString = "[" + surfaceForms.mkString(",") + "]"

              println(s"$labelString\t$entitiesListString\t$confidenceString\t$titleTypeString\t$occurrenceOffsetsString\t$surfaceFormsString")
              println()
            }
          }

        }
      )

  }

}
