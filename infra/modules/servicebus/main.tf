resource "azurerm_servicebus_namespace" "example" {
  name                = "${local.prefix}-servicebus-namespace"
  location            = var.location
  resource_group_name = var.resource_group_name
  sku                 = "Standard"

  tags = {
    source = "terraform"
  }
}

resource "azurerm_servicebus_topic" "example" {
  name         = "${local.prefix}-servicebus-topic"
  namespace_id = azurerm_servicebus_namespace.example.id

  partitioning_enabled = false
}
