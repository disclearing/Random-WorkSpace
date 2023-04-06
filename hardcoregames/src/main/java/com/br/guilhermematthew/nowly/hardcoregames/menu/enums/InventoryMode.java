package com.br.guilhermematthew.nowly.hardcoregames.menu.enums;

import lombok.Getter;

public enum InventoryMode {

	KIT_PRIMARIO("Selecione um kit primário"),
	KIT_SECUNDARIO("Selecione um kit secundário"),
    LOJA("Loja de Kits");
	
	@Getter
	private String inventoryName;
	
	InventoryMode(String inventoryName) {
		this.inventoryName = inventoryName;
	}
}