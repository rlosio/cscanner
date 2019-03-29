terraform {
  backend "s3" {
    bucket = "cscanner-terraform"
    key    = "terraform.tfstate"
    region = "us-east-1"
    dynamodb_table = "terraform"
  }
}
