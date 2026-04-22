resource "azurerm_network_security_group" "nsg" {
  name                = "${var.name}-nsg"
  location            = var.location
  resource_group_name = var.rg_name
}

# Allow SSH only from Bastion subnet
resource "azurerm_network_security_rule" "allow_ssh_from_bastion" {
  name                        = "Allow-SSH-From-Bastion"
  priority                    = 100
  direction                   = "Inbound"
  access                      = "Allow"
  protocol                    = "Tcp"
  source_address_prefix       = "103.40.202.30/32"
  destination_port_range      = "22"
  destination_address_prefix  = "*"
  source_port_range           = "*"
  resource_group_name         = var.rg_name
  network_security_group_name = azurerm_network_security_group.nsg.name
}

# Deny all SSH from internet
resource "azurerm_network_security_rule" "deny_ssh_public" {
  name                        = "Deny-SSH-Public"
  priority                    = 200
  direction                   = "Inbound"
  access                      = "Deny"
  protocol                    = "Tcp"
  source_address_prefix       = "*"
  destination_port_range      = "22"
  destination_address_prefix  = "*"
  source_port_range           = "*"
  resource_group_name         = var.rg_name
  network_security_group_name = azurerm_network_security_group.nsg.name
}

resource "azurerm_subnet_network_security_group_association" "vm" {
  subnet_id                 = var.subnet_id
  network_security_group_id = azurerm_network_security_group.nsg.id
}
