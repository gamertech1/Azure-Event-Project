module "eventygrid" {
  source      = "../../../modules/eventgrid"
  name        = "${var.prefix}-eg"
  location    = var.location
  org         = var.org
  project     = var.project
  environment = var.environment
}
module "servicebus" {
  source      = "../../../modules/servicebus"
  name        = "${var.prefix}-sb"
  location    = var.location
  org         = var.org
  project     = var.project
  environment = var.environment
}
