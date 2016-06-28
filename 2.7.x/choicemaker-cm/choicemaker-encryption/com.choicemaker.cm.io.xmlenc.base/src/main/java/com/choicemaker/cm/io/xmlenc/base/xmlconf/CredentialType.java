package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import java.util.Set;

public interface CredentialType {
	
	public String getTypeName();
	
	Set<String> getRequiredPropertyNames();
	
	Set<String> getPropertyNames();

}
