///usr/bin/env jbang "$0" "$@" ; exit $?

//JAVA 17
//DEPS org.neo4j.driver:neo4j-java-driver:5.27.0

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Logging;

public class hello_neo4j {
	private static final String uri = System.getenv("NEO4J_URI");
	private static final String username = System.getenv("NEO4J_USERNAME");
	private static final String password = System.getenv("NEO4J_PASSWORD");

	public static void main(String... args) {
		var driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));

		try (driver) {
            driver.verifyConnectivity();
            System.out.println("Connection established.");
        }
		
		// be kind and close the driver instance
		driver.close();
	}
}
