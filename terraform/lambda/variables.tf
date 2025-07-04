variable "region" {
  description = "AWS region to host your infrastructure"
  type        = string
}

variable "deploy_profile" {
  default = "dev"
}

variable "matched_participant_name" {
  type = string
}

variable "participant_queue_name" {
  type = string
}

variable "participant_queue_name_dlq" {
  type = string
}

variable "lambda_role_name" {
  type    = string
  default = "matcher_lambda_role"
}

variable "lambda_policy_name" {
  type    = string
  default = "matcher_lambda_policy"
}

variable "dynamoDB_name" {
  default = "participant_matcher"
}

variable "image_tag" {
  description = "Lambda image tag"
  type        = string
}

variable "matcher_lambda_arn" {
  type = string
}

variable "matcher_participant_url" {
  type = string
}