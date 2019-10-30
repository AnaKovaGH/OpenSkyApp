package messages


import io.circe._


case object StartWork
case object WorkStarted
case object CompleteWork
case object WorkCompleted
case object UnknownMessage

case object IngestDataFromDatasource
case class DataIngested(data: String)

case class TransformDataToJSON(data: String)
case class DataTransformed(data: Json)

case class CalculateData(data: Json)
case class DataCalculated(data: Json)

case class SendDataToKafka(data: Json)
case object DataSent

case object GetDataFromKafka

