resource "azurerm_eventgrid_topic" "example" {
  name                = "${local.prefix}-eventgrid-topic"
  location            = var.location
  resource_group_name = var.resource_group_name

  tags = {
    environment = "Production"
  }
}

resource "azurerm_eventgrid_system_topic_event_subscription" "example" {
  name                = "${local.prefix}-event-subscription"
  system_topic        = azurerm_eventgrid_topic.example.name
  resource_group_name = var.resource_group_name

  webhook_endpoint {
    url = "https://hkdk.events/7y1jh9fap44zvp"
  }
}
