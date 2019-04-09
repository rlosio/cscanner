provider "aws" {
  region = "at-vie-1"
  alias = "exoscale-sos"
  access_key = "${var.EXOSCALE_KEY}"
  secret_key = "${var.EXOSCALE_SECRET}"
  endpoints {
    s3 = "https://sos-at-vie-1.exo.io"
  }
  skip_credentials_validation = true
  skip_region_validation = true
  skip_requesting_account_id = true
}

resource "aws_s3_bucket" "exoscale-noncompliant" {
  provider = "aws.exoscale-sos"
  bucket = "cscanner-noncompliant"
  acl    = "public-read"
}
