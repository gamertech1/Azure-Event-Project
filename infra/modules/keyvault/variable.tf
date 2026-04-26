variable "name" {
  description = "Name of the Key Vault (must be globally unique)"
  type        = string
}

variable "location" {
  description = "Azure region for the Key Vault"
  type        = string
}
variable "environment" {}
variable "org" {}
variable "project" {}

variable "resource_group_name" {
  description = "Resource group name for the Key Vault"
  type        = string
}
variable "ssh_key_secret_name" {
  type = string
}
