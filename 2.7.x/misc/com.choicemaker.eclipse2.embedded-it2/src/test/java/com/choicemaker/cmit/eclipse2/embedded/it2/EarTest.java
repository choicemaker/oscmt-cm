package com.choicemaker.cmit.eclipse2.embedded.it2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.choicemaker.cmit.eclipse2.embedded.it2.HelloWorld;
import com.choicemaker.cmit.utils.DeploymentUtils;

@RunWith(Arquillian.class)
public class EarTest {
	
	private static final String MAVEN_COORDINATE_SEPARATOR = ":";
	
	static final String PROJECT_POM = "pom.xml";
	
	static final String DEPENDENCIES_POM = PROJECT_POM;
	
	static final String EJB_MAVEN_GROUPID =
		"com.choicemaker.fake";

	static final String EJB_MAVEN_ARTIFACTID =
			"com.choicemaker.fake.stateless-01";

	static final String EJB_MAVEN_VERSION =
			"0.0.1-SNAPSHOT";

	static final String EJB_MAVEN_COORDINATES = new StringBuilder()
			.append(EJB_MAVEN_GROUPID).append(MAVEN_COORDINATE_SEPARATOR)
			.append(EJB_MAVEN_ARTIFACTID).append(MAVEN_COORDINATE_SEPARATOR)
			.append(EJB_MAVEN_VERSION).toString();

	@Deployment
	public static EnterpriseArchive createEarArchive() {
		List<Class<?>> testClasses = new ArrayList<>();
		testClasses.add(EarTest.class);
		testClasses.add(com.choicemaker.cmit.eclipse2.embedded.it2.HelloWorld.class);
		testClasses.add(com.choicemaker.cmit.eclipse2.embedded.it2.HelloWorldBean.class);

		JavaArchive ejb =
				DeploymentUtils.createEjbJar(PROJECT_POM, null,
						testClasses, null);

		File[] deps = DeploymentUtils.createTestDependencies(DEPENDENCIES_POM);

		EnterpriseArchive retVal = DeploymentUtils.createEarArchive(ejb, deps);
		return retVal;
	}

	@EJB
	HelloWorld helloWorld;

	@Test
	public void testEJB() {
		// Exercise the EJB
		Assert.assertTrue (helloWorld != null);
		String hello = helloWorld.sayHello();
		Assert.assertTrue(hello != null);
		// System.out.println(this.getClass().getSimpleName() + ": " +hello);
	}

}