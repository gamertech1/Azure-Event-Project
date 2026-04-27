module "rg" {
  source      = "../../../modules/rg"
  component   = var.component
  location    = var.location
  environment = var.environment
  org         = var.org
  project     = var.project
}
