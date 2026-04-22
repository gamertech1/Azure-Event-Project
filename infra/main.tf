module "rg" {
  source   = "./modules/rg"
  prefix   = var.prefix
  location = var.location
}

module "keyvault" {
  source              = "./modules/keyvault"
  name                = "${var.prefix}-kv"
  location            = var.location
  resource_group_name = module.rg.rg-1
}
data "azurerm_key_vault_secret" "ssh_public_key" {
  name         = "bootstrap-vm-ssh-public-key"
  key_vault_id = module.keyvault.keyvault-1
  depends_on   = [module.keyvault]
}

module "vm" {
  source              = "./modules/vm"
  ssh_public_key      = data.azurerm_key_vault_secret.ssh_public_key.value
  depends_on          = [module.keyvault]
  resource_group_name = module.rg.rg-1
  location            = var.location
}

module "nsg" {
  source    = "./modules/nsg"
  name      = "${var.prefix}-nsg"
  location  = var.location
  rg_name   = module.rg.rg-1
  subnet_id = module.vm.subnet_id
}
module "eventygrid" {
  source   = "./modules/eventgrid"
  name     = "${var.prefix}-eg"
  location = var.location
}
module "servicebus" {
  source   = "./modules/servicebus"
  name     = "${var.prefix}-sb"
  location = var.location
}
