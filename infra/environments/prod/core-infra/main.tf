module "rg" {
  source      = "../../../modules/rg"
  layer       = var.layer
  location    = var.location
  environment = var.environment
  org         = var.org
  project     = var.project
}

module "keyvault" {
  source              = "../../../modules/keyvault"
  name                = "${var.prefix}-kv"
  location            = var.location
  resource_group_name = module.rg.rg-1
  environment         = var.environment
  org                 = var.org
  project             = var.project
  ssh_key_secret_name = "agent-pool-ssh-public-key"
}

module "vm" {
  source              = "../../../modules/vm"
  key_vault_id        = module.keyvault.keyvault-1
  ssh_key_secret_name = module.keyvault.ssh_key_secret_name
  depends_on          = [module.keyvault]
  resource_group_name = module.rg.rg-1
  location            = var.location
  environment         = var.environment
  org                 = var.org
  project             = var.project
}

module "nsg" {
  source      = "../../../modules/nsg"
  name        = "${var.prefix}-nsg"
  location    = var.location
  rg_name     = module.rg.rg-1
  subnet_id   = module.vm.subnet_id
  environment = var.environment
  org         = var.org
  project     = var.project
}
