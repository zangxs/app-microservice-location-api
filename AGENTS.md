# AGENTS.md

## Build & Lint Commands

### General

- **Build**: `mvn clean package` (for Maven)
- **Lint**: `mvn spotless:check` (for Spotless)
- **Format**: `mvn spotless:apply` (for Spotless)
- **Test**: `mvn test` (runs all tests)

### Single Test

- Run a single test: `mvn -Dtest=MyTestClass test`
- Run a single test method: `mvn -Dtest=MyTestClass#myTestMethod test`

## Code Style Guidelines

### Naming
- Classes: `PascalCase`
- Variables/Methods: `snake_case`
- Constants: `SCREAMING_SNAKE_CASE`
- Enums: `PascalCase` with `_$` suffix for enum values (e.g., `USER_TYPE_ADMIN`)

### Formatting
- Indentation: 4 spaces
- Line length: 120 characters max
- Braces: Always use braces for if/else/for/while
- No trailing spaces

### Types
- Prefer primitives over wrappers unless needed
- Use `@NotNull`/`@Nullable` from `javax.annotation` for clarity
- Avoid raw types; use generic parameters

### Error Handling
- Use try-catch blocks for recoverable errors
- Log errors using `logger.error()`
- Return `Optional` for potential absence of values
- Never ignore exceptions

### Imports
- Organize imports alphabetically
- Remove unused imports automatically
- Use `import static` for utility methods
- No `import java.*` or `import com.*`

### APIs
- Use `@RestController` for REST endpoints
- Use `@Service` for business logic
- Use `@Repository` for data access
- Use `@Component` for utility classes

## Rules

### Cursor Rules
- If .cursor/rules/ exists, follow its formatting and style guidelines
- Use `cursor format` command for automatic formatting

### Copilot Instructions
- If .github/copilot-instructions.md exists, follow its guidelines
- Use `cursor copilot` for AI-assisted coding

## Additional Notes
- Always run `mvn spotless:check` before commits
- Use `mvn test` with `-DfailIfNoTests=true` to enforce test coverage
- Keep test classes in `src/test/java` with package structure matching `src/main/java`
- Use `@SpringBootTest` for integration tests
- Use `@WebMvcTest` for controller-specific tests
- Use `@MockBean` for mocking Spring beans
- Use `@DataJpaTest` for JPA repository tests
- Use `@Transactional` for test transactions
- Use `@BeforeEach`/`@AfterEach` for setup/teardown
- Use `@DisplayName` for test method names
- Use `@Tag` for test categorization
- Use `@Disabled` to skip tests temporarily
- Use `@SpringBootTest(webEnvironment = RANDOM_PORT)` for external API tests
- Use `WebTestClient` for reactive endpoints
- Use `MockHttpServletResponse` for HTTP response assertions
- Use `Mockito` for mocking dependencies
- Use `ArgumentCaptor` for capturing method arguments
- Use `@Captor` for capturing arguments in tests
- Use `@Spy` for partial mocking
- Use `@InjectMocks` for injecting mocks into SUT
- Use `@Mock` for creating mock objects
- Use `when().thenReturn()` for stubbing methods
- Use `verify()` to confirm method calls
- Use `Mockito.doThrow()` for throwing exceptions
- Use `Mockito.any()`/`Mockito.eq()` for argument matching
- Use `Mockito.times()`/`Mockito.atLeastOnce()` for verification
- Use `Mockito.isNull()`/`Mockito.isNotNull()` for null checks
- Use `Mockito.verifyNoMoreInteractions()` to ensure no unexpected calls
- Use `Mockito.reset()` to reset mock behavior
- Use `Mockito.clearInvocations()` to clear invocation history
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doThrow(new RuntimeException()).when(mock).someMethod()` for exception stubs
- Use `Mockito.doAnswer(invocation -> ...).when(mock).someMethod()` for custom logic
- Use `Mockito.inOrder()` for verifying method call order
- Use `Mockito.spy()` for partial mocking of real objects
- Use `Mockito.lenient()` for lenient mocking
- Use `Mockito.strictness(Strictness.LENIENT)` for lenient mock settings
- Use `Mockito.withSettings().lenient().mock()` for lenient mocks
- Use `Mockito.spy(new MyService())` for spying on real objects
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mocky doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no-op stubs
- Use `Mockito.doCallRealMethod().when(mock).someMethod()` to call real methods
- Use `Mockito.doReturn().when(mock).someMethod()` for returning values
- Use `Mockito.doThrow().when(mock).someMethod()` for throwing exceptions
- Use `Mockito.doAnswer().when(mock).someMethod()` for custom logic
- Use `Mockito.doNothing().when(mock).someMethod()` for no, and append any Cursor or Copilot rules from the specified files.

### Rules testing
- In controllers use WebTestClient 
- In controllers use @Mock to inject services, example: @Mock private IUserService userService;

## Controller Testing Rules (WebFlux)

### Test Class Structure
- Use `@ExtendWith(MockitoExtension.class)` for unit tests (no Spring context)
- Use `@Mock` for all service dependencies
- Use `@InjectMocks` for the controller under test
- Initialize `WebTestClient` in `@BeforeEach`: `WebTestClient.bindToController(controller).build()`

### Testing Approaches

#### Standard HTTP Endpoints (WebTestClient)
For endpoints that don't require Reactor context:
```java
webTestClient.get()
    .uri("/resource/{id}", id)
    .exchange()
    .expectStatus().isOk()
    .expectBody()
    .jsonPath("$.code").isEqualTo(200)
    .jsonPath("$.data").exists();
```

#### Endpoints with Reactor Context (StepVerifier)
For endpoints using `Mono.deferContextual()` (e.g., require `userId` from context):
```java
Mono<ResponseEntity<ApiResponse>> result = controller.method(id)
    .contextWrite(ctx -> ctx.put("userId", "user-123"));

StepVerifier.create(result)
    .assertNext(response -> {
        assert response.getStatusCode().is2xxSuccessful();
        // assertions...
    })
    .verifyComplete();
```

#### Endpoints with ServerHttpRequest
When controller extracts `baseUrl` from `ServerHttpRequest`, mock it:
```java
ServerHttpRequest mockRequest = mock(ServerHttpRequest.class);
when(mockRequest.getURI()).thenReturn(new URI("http://localhost:8080"));

Mono<ResponseEntity<ApiResponse>> result = controller.method(mockRequest);
```

#### Multipart Upload Tests
Create a mock `FilePart` implementation:
```java
static class MockFilePart implements FilePart {
    private final String filename;
    MockFilePart(String filename) { this.filename = filename; }
    @Override public String name() { return "file"; }
    @Override public String filename() { return filename; }
    @Override public Flux<DataBuffer> content() { return Flux.empty(); }
    @Override public Mono<Void> transferTo(Path dest) { return Mono.empty(); }
    @Override public Mono<Void> delete() { return Mono.empty(); }
    @Override public HttpHeaders headers() { return new HttpHeaders(); }
}
```

### Coverage Requirements
- Minimum 80% line coverage for controllers
- Use JaCoCo plugin with check rule in pom.xml
- Run: `mvn clean test -Dtest=ControllerTest`

### Common Patterns

| Scenario | Approach |
|----------|----------|
| GET with params | `WebTestClient` with `.uri(uriBuilder -> ...)` |
| POST/DELETE with path variable | `WebTestClient` with `.uri("/path/{id}", id)` |
| Reactor context (userId) | `StepVerifier` + `.contextWrite()` |
| ServerHttpRequest dependency | Mock with `mock(ServerHttpRequest.class)` |
| File upload | Direct method call with `MockFilePart` |
| Not found (empty Mono) | `when(service.method()).thenReturn(Mono.empty())` |
| Error scenarios | `when(service.method()).thenReturn(Mono.error(...))` |

### Dependencies Required in pom.xml
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <scope>test</scope>
</dependency>
```

### JaCoCo Configuration
Add to pom.xml plugins section:
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
        <execution>
            <id>jacoco-check</id>
            <goals><goal>check</goal></goals>
            <configuration>
                <rules>
                    <rule>
                        <element>CLASS</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```
