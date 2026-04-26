module "rg" {
  source      = "../../../modules/rg"
  layer       = var.layer
  location    = var.location
  environment = var.environment
  org         = var.org
  project     = var.project
}

module "eventygrid" {
  source              = "../../../modules/eventgrid"
  location            = var.location
  org                 = var.org
  project             = var.project
  environment         = var.environment
  resource_group_name = module.rg.name.rg-1
}
module "servicebus" {
  source              = "../../../modules/servicebus"
  location            = var.location
  org                 = var.org
  project             = var.project
  environment         = var.environment
  resource_group_name = module.rg.name.rg-1
}
