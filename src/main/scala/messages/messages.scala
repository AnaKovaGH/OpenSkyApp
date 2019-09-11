package messages


case object StartMessage
case object IngestDataMessage
case class TransformDataToJSONMessage(data: String)
case class CalculateDataMessage(data: String)
case class SendDataToKafkaMessage(data: String)
case object  CompleteWork