terraform {
  backend "s3" {}
}

provider "aws" {}


resource "aws_iam_role" "lambda_role" {
  name = var.lambda_role_name
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = { Service = "lambda.amazonaws.com" }
        Action    = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy" "lambda_policy" {
  name = var.lambda_role_name
  role = aws_iam_role.lambda_role.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:*",
          "dynamodb:*",
          "sqs:*"
        ]
        Resource = "*"
      }
    ]
  })
}

resource "aws_lambda_function" "matcher_lambda" {
  function_name = "matcher-lambda-${var.deploy_profile}"
  role          = aws_iam_role.lambda_role.arn
  handler       = "Main::handleRequest"
  runtime       = "java21"
  memory_size   = 512
  timeout       = 30

  s3_bucket = "skillzzy-matcher-terraform-lambda"
  s3_key    = "build-${var.deploy_profile}-matcher-lambda-tstates/matcher-lambda-${var.image_tag}.jar"

  environment {
    variables = {
      DB_TABLE_NAME                 = "${var.dynamoDB_name}_${var.deploy_profile}"
      MATCHED_PARTICIPANT_QUEUE_URL = var.matcher_participant_url
    }
  }
}

resource "aws_lambda_event_source_mapping" "sqs_event" {
  event_source_arn = var.participant_queue_arn
  function_name    = aws_lambda_function.matcher_lambda.arn
  batch_size       = 1
}
