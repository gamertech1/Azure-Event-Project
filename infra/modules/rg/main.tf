resource "azurerm_resource_group" "rg-tfstate-bootstrap" {
  name     = "${local.prefix}-rg-${var.component}"
  location = var.location
}
