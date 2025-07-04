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
        Effect = "Allow"
        Principal = { Service = "lambda.amazonaws.com" }
        Action = "sts:AssumeRole"
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

resource "aws_dynamodb_table" "matches" {
  name         = "${var.dynamoDB_name}_${var.deploy_profile}"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "id"

  attribute {
    name = "id"
    type = "N"
  }
}

resource "aws_lambda_function" "matcher_lambda" {
  function_name = "matcher-lambda-${var.deploy_profile}"
  role          = aws_iam_role.lambda_role.arn
  handler       = "MatcherLambda-${var.deploy_profile}"
  runtime       = "java21"
  memory_size   = 512
  timeout       = 30

  environment {
    variables = {
      TABLE_NAME                    = aws_dynamodb_table.matches.name
      MATCHED_PARTICIPANT_QUEUE_URL = aws_sqs_queue.matcher_queue.url
    }
  }
}

resource "aws_lambda_event_source_mapping" "sqs_event" {
  event_source_arn = aws_sqs_queue.matcher_queue.arn
  function_name    = aws_lambda_function.matcher_lambda.arn
  batch_size       = 1
}
