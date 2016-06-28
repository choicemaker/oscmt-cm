package com.choicemaker.cm.io.xmlenc.base.xmlconf;

import com.amazonaws.auth.AWSCredentials;
import com.choicemaker.util.Precondition;
import com.choicemaker.xmlencryption.AwsKmsUtils;

public class AwsKmsEncryptionCredential extends EncryptionCredential {

	public AwsKmsEncryptionCredential(String name, String masterKeyId,
			String endpoint) {
		this(AwsKmsUtils.getDefaultAWSCredentials(), name, masterKeyId,
				endpoint);
	}

	public AwsKmsEncryptionCredential(AWSCredentials aws, String name,
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
		this.put(AwsKmsEncryptionScheme.PN_SECRETKEY, secretKeyId);
		String accessKeyId = aws.getAWSAccessKeyId();
		this.put(AwsKmsEncryptionScheme.PN_ACCESSKEY, accessKeyId);
		this.put(AwsKmsEncryptionScheme.PN_MASTERKEY, masterKeyId);
		if (endpoint != null) {
			this.put(AwsKmsEncryptionScheme.PN_ENDPOINT, endpoint);
		}
	}

}
