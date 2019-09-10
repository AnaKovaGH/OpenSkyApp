package messages


case object StartMessage
case object IngestMessage
case class IngestedDataMessage(data: String)
case class TransformMessage(data: String)
case class TransformedDataMessage(data: String)