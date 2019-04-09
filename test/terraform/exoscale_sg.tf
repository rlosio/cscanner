provider "exoscale" {
  region = "at-vie-1"
  alias = "exoscale-sg"
  key = "${var.EXOSCALE_KEY}"
  secret = "${var.EXOSCALE_SECRET}"
}

resource "exoscale_security_group" "noncompliant" {
  provider = "exoscale.exoscale-sg"
  name = "noncompliant"
}

resource "exoscale_security_group_rules" "noncompliant" {
  provider = "exoscale.exoscale-sg"
  security_group_id = "${exoscale_security_group.noncompliant.id}"

  ingress {
    protocol = "TCP"
    cidr_list = ["0.0.0.0/0", "::/0"]
    ports = ["22"]
    cidr_list = ["0.0.0.0/0"]
  }
}

resource "exoscale_security_group" "compliant" {
  provider = "exoscale.exoscale-sg"
  name = "compliant"
}
