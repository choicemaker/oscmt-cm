package com.choicemaker.e2.platform;

import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.CMExtensionPoint;
import com.choicemaker.e2.CMPluginDescriptor;
import com.choicemaker.e2.CMPluginRegistry;

public class DoNothingRegistry implements CMPluginRegistry {

	private static final DoNothingRegistry instance = new DoNothingRegistry();

	public static final DoNothingRegistry getInstance() {
		return instance;
	}

	private DoNothingRegistry() {
	}

	@Override
	public CMConfigurationElement[] getConfigurationElementsFor(
			String extensionPointId) {
		return new CMConfigurationElement[0];
	}

	@Override
	public CMConfigurationElement[] getConfigurationElementsFor(String pluginId,
			String extensionPointName) {
		return new CMConfigurationElement[0];
	}

	@Override
	public CMConfigurationElement[] getConfigurationElementsFor(String pluginId,
			String extensionPointName, String extensionId) {
		return new CMConfigurationElement[0];
	}

	@Override
	public CMExtension getExtension(String extensionPointId,
			String extensionId) {
		return null;
	}

	@Override
	public CMExtension getExtension(String pluginId, String extensionPointName,
			String extensionId) {
		return null;
	}

	@Override
	public CMExtensionPoint getExtensionPoint(String extensionPointId) {
		return DoNothingExtensionPoint.getInstance();
	}

	@Override
	public CMExtensionPoint getExtensionPoint(String pluginId,
			String extensionPointName) {
		return DoNothingExtensionPoint.getInstance();
	}

	@Override
	public CMExtensionPoint[] getExtensionPoints() {
		return new CMExtensionPoint[0];
	}

	@Override
	public CMPluginDescriptor getPluginDescriptor(String pluginId) {
		return null;
	}

	@Override
	public CMPluginDescriptor[] getPluginDescriptors() {
		return new CMPluginDescriptor[0];
	}

	@Override
	public CMPluginDescriptor[] getPluginDescriptors(String pluginId) {
		return new CMPluginDescriptor[0];
	}

}
