resource "azurerm_resource_group" "rg-tfstate-bootstrap" {
  name     = "${var.prefix}-resources"
  location = var.location
}
