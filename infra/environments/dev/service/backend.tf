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
    key                  = "dev.service.terraform.tfstate"
  }
}
