provider "aws" {
  alias = "aws-s3"
  region = "us-east-1"
  access_key = "${var.AWS_ACCESS_KEY_ID}"
  secret_key = "${var.AWS_SECRET_ACCESS_KEY}"
}

resource "aws_s3_bucket" "noncompliant" {
  provider = "aws.aws-s3"
  bucket = "cscanner-noncompliant"
  acl    = "public-read"
}