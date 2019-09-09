package messages


case object StartMessage
case class IngestedDataMessage(data: String)
case object TransformedDataMessage