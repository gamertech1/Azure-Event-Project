terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "=4.1.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.0"
    }
  }
   backend "azurerm" {
    resource_group_name  = "rg-bootstrap"
    storage_account_name = "bootstrapsa1234"
    container_name       = "bootstrap"
    key                  = "prod.terraform.tfstate"
  }
}


provider "azurerm" {
  features {
    resource_group {
      prevent_deletion_if_contains_resources = false
    }
  }
  subscription_id = "41b5e51d-76b5-4464-a7c4-5530c37b0cea"
}
