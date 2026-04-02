package snippet;

public class Snippet {
	# PostgreSQL (Render)
	spring.datasource.url=${DATABASE_URL}
	spring.datasource.driver-class-name=org.postgresql.Driver
	
	spring.jpa.hibernate.ddl-auto=update
	spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
	
	# JWT (same, but from env for safety)
	app.jwt.secret=${JWT_SECRET}
	app.jwt.expiration=86400000
	
	# CORS (update after Vercel deploy)
	app.cors.allowed-origins=https://your-app.vercel.app
	
	# File upload (same)
	spring.servlet.multipart.max-file-size=10MB
	spring.servlet.multipart.max-request-size=10MB
}

