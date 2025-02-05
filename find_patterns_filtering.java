///usr/bin/env jbang "$0" "$@" ; exit $?

//JAVA 17
//DEPS org.neo4j.driver:neo4j-java-driver:5.27.0
//DEPS org.neo4j:neo4j-cypher-dsl:2024.4.0

import java.util.Map;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Logging;

import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.renderer.Renderer;

public class find_patterns_filtering {

	private static final String uri = System.getenv("NEO4J_URI");
	private static final String username = System.getenv("NEO4J_USERNAME");
	private static final String password = System.getenv("NEO4J_PASSWORD");
	private static final Renderer cypherRenderer = Renderer.getDefaultRenderer();

	public static void main(String... args) {
		var driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));

		// find orders containing > 2 lattes
		var product = Cypher.node("Product").named("p")
						.withProperties("productName", Cypher.literalOf("Latte"));
		var orders = Cypher.node("Order").named("o");
		var containsRel = product.relationshipFrom(orders, "CONTAINS").named("rel");
		var quantity = containsRel.property("quantity");
		var query = Cypher.match(containsRel)
						.where(containsRel.property("quantity").gt(Cypher.literalOf(2)))
						.returning(orders.property("transactionId"),
									product.property("productName"),
									quantity)
						.orderBy(quantity.descending())
						.limit(10)
						.build();

		try (driver) {
			try (var session = driver.session()) {
                try {
					var result = driver.executableQuery(cypherRenderer.render(query)).execute();
					
					System.out.println("Found:");
					var records = result.records();
					records.forEach(r -> {
						System.out.println(r.fields());  // or r.get("name").asString()
					});
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

		// be kind and close the driver instance
		driver.close();
	}
}
