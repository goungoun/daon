package daon.dictionary.spark

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}

object SparkCustom {

  val corpusFilePath = "/Users/mac/work/corpus/sejong_utagger.json"

  val irrFilePath = "/Users/mac/work/corpus/sejong_utagger_irr.json"

  case class Keyword(word: String, tag: String, tf: Long, prop: Double)


  def main(args: Array[String]) {


//    val spark = SparkSession
//      .builder()
//      .appName("daon dictionary")
//      .master("local[*]")
//      .getOrCreate()



    val str = "강남"
    val pos = "NNG"


//    val hash = Utils.hashCode(str + pos)
    val hash = new String(str + pos).hashCode

    println(hash)


//    val text = "이밖에 임야 및 공장·밭·논 등 산업 용지는 매년 줄어드는 반면 대지·종교용지 등은 늘어나는 등 도시개발이 지속되고 있으며 하루사이 4백97대의 차량이 증가하고 9백56건의 범죄가 발생하는 등 도시문제가 갈수록 심각해지고 있다.<강승규·김화균기자>"
    val text = "123"

    val results = text.split("[^가-힣]")

    println(results.size)
    results.foreach(println)


    val words = ArrayBuffer[String]()

    words += ("나이키", "아디다스")
    words += "아디다스"

    println(words)

    val wordSeqs = ArrayBuffer[Long]()

    breakable {
      for (i <- 0 to (10 - 1)) {

        if (i > 5) {
          break
        }

        wordSeqs += i
      }
    }

    println(wordSeqs)

    //    val replaceHashTag = udf[String, String]( _.replaceAll("[#$&\\.\\,\"\']", "").toLowerCase )
//    val tagJDBC = sqlContext.read.format("jdbc").options(Map(
//      "dbtable" -> "new_pikicast_common.TAG",
//      "numPartitions" -> "30") ++ mysqlConInfo).load.select("tag_id", "title")
//      .withColumn("title", replaceHashTag(col("title")))

//    readJsonWriteParquet(spark)

  }

}