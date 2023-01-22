package me.shedaniel.rei.plugin.client.exclusionzones;

import java.awt.Rectangle;
import java.util.Collection;

import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;

public class DefaultPotionEffectExclusionZones {
	public Collection<Rectangle> provide(EffectRenderingInventoryScreen<?> screen) {
		throw new AssertionError();
	}
}
