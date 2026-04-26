variable "resource_group_name" {
  description = "Resource group name for the Key Vault"
  type        = string
}
variable "location" {
  description = "Location of the resources"
  type        = string
}
variable "environment" {}
variable "org" {}
variable "project" {}
variable "key_vault_id" {}
variable "ssh_key_secret_name" {
  type = string
}
