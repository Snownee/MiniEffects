package me.shedaniel.rei.api.client.config;

public interface ConfigObject {
	static ConfigObject getInstance() {
		throw new AssertionError();
	}

	boolean isLeftSideMobEffects();
}
