terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.16"
    }
  }
  required_version = "1.1.7"
}

provider "aws" {
  region = "ap-south-1"
}

resource "aws_sns_topic" "notifications" {
  name = "notifications-${var.stage}"
}