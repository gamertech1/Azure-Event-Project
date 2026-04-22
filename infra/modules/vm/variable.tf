variable "ssh_public_key" {
  description = "SSH public key for VM access"
  type        = string
}
variable "resource_group_name" {
  description = "Resource group name for the Key Vault"
  type        = string
}
variable "location" {
  description = "Location of the resources"
  type        = string
}
