data "aws_sqs_queue" "participant_queue" {
  name = var.participant_queue_name
}

data "aws_sqs_queue" "matcher_participant" {
  name = var.matched_participant_name
}