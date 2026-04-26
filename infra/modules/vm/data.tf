data "azurerm_key_vault_secret" "ssh_public_key" {
  name         = var.ssh_key_secret_name
  key_vault_id = var.key_vault_id
}
