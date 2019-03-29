provider "aws" {
  alias = "aws-sg"
  region = "us-east-1"
  access_key = "${var.AWS_ACCESS_KEY_ID}"
  secret_key = "${var.AWS_SECRET_ACCESS_KEY}"
}

resource "aws_vpc" "noncompliant" {
  provider = "aws.aws-sg"
  cidr_block = "10.0.0.0/16"
}

resource "aws_security_group" "noncompliant" {
  provider = "aws.aws-sg"
  vpc_id = "${aws_vpc.noncompliant.id}"
  name = "noncompliant"
}

resource "aws_security_group_rule" "noncompliant" {
  provider = "aws.aws-sg"
  from_port = 22
  to_port = 22
  protocol = "tcp"
  security_group_id = "${aws_security_group.noncompliant.id}"
  type = "egress"
  cidr_blocks = ["0.0.0.0/0"]
}
