variable "rg_name" {
  description = "Resource group name"
  type        = string
}
variable "location" {
  description = "Azure region for the Network Security Group"
  type        = string
}
variable "subnet_id" {
  description = "ID of the subnet to associate with the NSG"
  type        = string
}
variable "environment" {}
variable "org" {}
variable "project" {}
