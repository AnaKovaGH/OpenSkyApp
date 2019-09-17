package messages

import io.circe._


case object StartMessage
case object IngestDataMessage
case class TransformDataToJSONMessage(data: String)
case class CalculateDataMessage(data: Json)
case class SendDataToKafkaMessage(data: String)//!TEMPORARY!
case object  CompleteWork