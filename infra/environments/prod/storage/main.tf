module "rg" {
  source      = "../../../modules/rg"
  layer       = var.layer
  location    = var.location
  environment = var.environment
  org         = var.org
  project     = var.project
}
