package messages


import io.circe._


case object StartWork
case object IngestDataFromDatasource
case class TransformDataToJSON(data: String)
case class CalculateData(data: Json)
case class SendDataToKafka(data: Json)
case object CompleteWork
case object GetDataFromKafka