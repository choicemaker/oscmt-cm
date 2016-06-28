package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import java.util.Properties;
import java.util.Set;

public interface EncryptionCredentials {
	
	Set<CredentialType> getCredentialTypes();
	
	Properties getCredentialProperties(CredentialType type);
	
	void putCredentialProperties(CredentialType type, Properties properties);

}
