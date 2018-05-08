package daon.spark.words
import java.io.InputStream
import java.util

import daon.core.data.{Morpheme, Word}
import daon.core.util.Utils
import daon.spark.AbstractWriter
import org.apache.spark.sql._

import scala.collection.mutable.ArrayBuffer

object UploadUserWords extends AbstractWriter {

  def main(args: Array[String]) {

    val spark = getSparkSession()

    val text =
      """
         하나 둘
         하나,하나/VV,1
         하나,하/NNG 만/NNB,1
      """

    import java.io.ByteArrayInputStream
    val input = new ByteArrayInputStream(text.getBytes())
    import scala.collection.JavaConverters._

    val errors = execute(spark, "/Users/mac/work/corpus/final_version/niadic_test.csv", "test", isAppend = false).asScala

    errors.foreach(println)
  }

  override def getJobName() = "upload_user_words"

  def execute(spark: SparkSession, path: String, prefix: String, isAppend: Boolean): util.List[String] = {
    import scala.collection.JavaConverters._

    val version = CONFIG.getString("index.words.version")
    val scheme = CONFIG.getString("index.words.scheme")
    val typeName = CONFIG.getString("index.words.type")

    val indexName = s"${prefix}_words_$version"

    createIndex(indexName, scheme)

    import org.apache.spark.sql.types.{StructType, StructField, StringType, IntegerType}

    val customSchema = StructType(Array(
       StructField("word", StringType, true),
       StructField("type", StringType, true),
       StructField("weight", StringType, true)        
    ))

    val df = spark.read.format("com.databricks.spark.csv").schema(customSchema).load(path)

    import spark.implicits._

    import scala.collection.JavaConverters._

    implicit val WordEncoder: Encoder[Word] = Encoders.bean(classOf[Word])

    val errors = ArrayBuffer[String]()

    val words_df = df.map(row =>{
      //val size = row.length // 빈 값이 null로 할당 (나이키, null, null)

      try {
        row match {
          case Row(t1, null, null) => {
            val surface = readSurface(row.getString(0))
            val word = new Word(surface, ArrayBuffer(new Morpheme(surface, "NNG")).asJava, 1)

            word
          }
          case Row(t1, t2, null) => {
            val surface = readSurface(row.getString(0))
            val weight = row.getString(1).toInt // on error if not number
            val word = new Word(surface, ArrayBuffer(new Morpheme(surface, "NNG")).asJava, weight)

            word
          }
          case Row(t1, t2, t3) => {
            val surface = readSurface(row.getString(0))
            val morphemes = Utils.parseMorpheme(row.getString(1)) // error if not parsed
            val weight = row.getString(2).toInt  // error if not number
            val word = new Word(surface, morphemes, weight)

            word
          }
          case _ => {
            throw new Exception("처리할수 없음")
          }
        }
      } catch {
        case e: Exception => {
          errors += s"${row.getString(0)} row -> ${e.getMessage}"

          println(s"${row.getString(0)} row -> ${e.getMessage}")
//          val word = new Word(s"$i 번째 row -> ${e.getMessage}", null, -1)
          null
        }
      }

    }).as(WordEncoder)

    val words_df2 = words_df.filter(w => w != null)

//    val rows = csvParse(input)

//    val (df, errors) = readRows(spark, rows)

    val mode = if(isAppend) "append" else "overwrite"

    words_df2.write.format("org.elasticsearch.spark.sql").mode(mode).save(s"$indexName/$typeName")

//    errors.foreach(println)

    addAlias(indexName, "words")

    errors.asJava
  }

  def csvParse(input: InputStream): util.List[Array[String]] = {
    import com.univocity.parsers.csv.CsvParserSettings
    val settings = new CsvParserSettings
    settings.getFormat.setLineSeparator("\n")

    import com.univocity.parsers.csv.CsvParser
    val parser = new CsvParser(settings)

    val rows = parser.parseAll(input)

    rows
  }


  def readRows(spark: SparkSession, list: util.List[Array[String]]): (Dataset[Word], ArrayBuffer[String]) = {
    import spark.implicits._

    import scala.collection.JavaConverters._

    val words = ArrayBuffer[Word]()
    val errors = ArrayBuffer[String]()

    val rows = list.asScala
    rows.indices.foreach(i => {
      val row = rows(i)
//      println(row.mkString(", "))

      val size = row.length

      try {
        size match {
          case 1 => {
            val surface = readSurface(row(0))
            val word = new Word(surface, ArrayBuffer(new Morpheme(surface, "NNG")).asJava, 1)

            words += word
          }
          case 2 => {
            val surface = readSurface(row(0))
            val weight = row(1).toInt // on error if not number
            val word = new Word(surface, ArrayBuffer(new Morpheme(surface, "NNG")).asJava, weight)

            words += word
          }
          case 3 => {
            val surface = readSurface(row(0))
            val morphemes = Utils.parseMorpheme(row(1)) // error if not parsed
            val weight = row(2).toInt // error if not number
            val word = new Word(surface, morphemes, weight)

            words += word
          }
          case _ => {
            throw new Exception("처리할수 없음")
          }
        }
      } catch {
        case e: Exception => {
          errors += s"$i 번째 row -> ${e.getMessage}"
        }
      }

    })

    implicit val WordEncoder: Encoder[Word] = Encoders.bean(classOf[Word])
    val df = words.toDF().as(WordEncoder)

    (df, errors)
  }

  def readSurface(str: String): String = {
    if(str.isEmpty){
      throw new Exception("surface 값이 없습니다.")
    }

    val trimmedStr = str.trim

    if("\\s".r.findAllIn(trimmedStr).nonEmpty){
      throw new Exception("surface 값에 공백이 들어있습니다.")
    }

    trimmedStr
  }

}
