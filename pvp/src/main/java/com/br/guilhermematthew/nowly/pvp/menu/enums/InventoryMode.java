package com.br.guilhermematthew.nowly.pvp.menu.enums;

import lombok.Getter;

public enum InventoryMode {

	KIT_PRIMARIO("Selecione o kit Primário"),
	KIT_SECUNDARIO("Selecione o kit Secundario"),
    LOJA("Loja de Kits");
	
	@Getter
	private String inventoryName;
	
	InventoryMode(String inventoryName) {
		this.inventoryName = inventoryName;
	}
}