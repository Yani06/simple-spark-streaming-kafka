import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.codehaus.jackson.map.deser.std.StringDeserializer
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe


object MainApp  extends  App {
  println("Started....")

  val sparkSession = SparkSession.builder().config("", "").getOrCreate()
  import sparkSession.sqlContext.implicits._
  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "localhost:9092",
    "key.deserializer" ->  classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "myGroup",
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> false
  )

  val myTopic = Array("test-topic")
  val sparkStreamingCtx = new StreamingContext(sparkSession.sparkContext, Seconds(10))
  val stream = KafkaUtils.createDirectStream (sparkStreamingCtx, PreferConsistent, Subscribe(myTopic, kafkaParams))

  val rdd = stream.map(r => r.value)
  sparkStreamingCtx.start()
  sparkStreamingCtx.awaitTermination()

}
