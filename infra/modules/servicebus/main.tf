resource "azurerm_servicebus_namespace" "example" {
  name                = "tfex-servicebus-namespace"
  location            = var.location
  resource_group_name = var.name
  sku                 = "Standard"

  tags = {
    source = "terraform"
  }
}

resource "azurerm_servicebus_topic" "example" {
  name         = "tfex_servicebus_topic"
  namespace_id = azurerm_servicebus_namespace.example.id

  partitioning_enabled = false
}
