output "keyvault-1" {
  value = azurerm_key_vault.example.id
}

output "ssh_key_secret_name" {
  value = var.ssh_key_secret_name
}
