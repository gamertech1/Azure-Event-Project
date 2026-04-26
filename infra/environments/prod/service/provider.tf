provider "azurerm" {
  features {
    resource_group {
      prevent_deletion_if_contains_resources = false
    }
  }
  subscription_id = "41b5e51d-76b5-4464-a7c4-5530c37b0cea"
}
