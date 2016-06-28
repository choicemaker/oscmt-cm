package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import com.amazonaws.auth.AWSCredentials;
import com.choicemaker.util.Precondition;
import com.choicemaker.xmlencryption.AwsKmsUtils;

public class DefaultAWSEncryptionCredential extends EncryptionCredential {

	public DefaultAWSEncryptionCredential(String name, String masterKeyId,
			String endpoint) {
		this(AwsKmsUtils.getDefaultAWSCredentials(), name, masterKeyId,
				endpoint);
	}

	public DefaultAWSEncryptionCredential(AWSCredentials aws, String name,
			String masterKeyId, String endpoint) {
		super(name);
		Precondition.assertNonNullArgument("null AWS credentials", aws);
		Precondition.assertNonEmptyString("null or blank masterKeyId",
				masterKeyId);
		if (endpoint != null) {
			endpoint = endpoint.trim();
			if (endpoint.isEmpty()) {
				endpoint = null;
			}
		}

		String secretKeyId = aws.getAWSAccessKeyId();
		this.put(DefaultEncryptionPolicy.PN_SECRETKEY, secretKeyId);
		String accessKeyId = aws.getAWSAccessKeyId();
		this.put(DefaultEncryptionPolicy.PN_ACCESSKEY, accessKeyId);
		this.put(DefaultEncryptionPolicy.PN_MASTERKEY, masterKeyId);
		if (endpoint != null) {
			this.put(DefaultEncryptionPolicy.PN_ENDPOINT, endpoint);
		}
	}

}
