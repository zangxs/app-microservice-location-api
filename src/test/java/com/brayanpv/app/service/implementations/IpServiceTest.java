package com.brayanpv.app.service.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpServiceTest {

    @Mock
    private WebClient webClient;

    private IpService ipService;

    @BeforeEach
    void setUp() {
        ipService = new IpService(webClient);
    }

    @Test
    void getCoordinatesLocalhostIpv4() {
        Mono<double[]> result = ipService.getCoordinates("127.0.0.1");

        StepVerifier.create(result)
                .assertNext(coords -> {
                    assert coords[0] == 4.5709;
                    assert coords[1] == -74.2973;
                })
                .verifyComplete();
    }

    @Test
    void noArgsConstructorCreatesServiceWithDefaultWebClient() {
        IpService defaultService = new IpService();
        Mono<double[]> result = defaultService.getCoordinates("127.0.0.1");

        StepVerifier.create(result)
                .assertNext(coords -> {
                    assert coords[0] == 4.5709;
                    assert coords[1] == -74.2973;
                })
                .verifyComplete();
    }

    @Test
    void getCoordinatesLocalhostIpv6() {
        Mono<double[]> result = ipService.getCoordinates("0:0:0:0:0:0:0:1");

        StepVerifier.create(result)
                .assertNext(coords -> {
                    assert coords[0] == 4.5709;
                    assert coords[1] == -74.2973;
                })
                .verifyComplete();
    }

    @Test
    void getCoordinatesPrivateNetwork() {
        Mono<double[]> result = ipService.getCoordinates("192.168.1.100");

        StepVerifier.create(result)
                .assertNext(coords -> {
                    assert coords[0] == 4.5709;
                    assert coords[1] == -74.2973;
                })
                .verifyComplete();
    }

    @Test
    void getCoordinatesExternalIpSuccess() throws Exception {
        String jsonResponse = "{\"lat\": 40.7128, \"lon\": -74.0060}";
        JsonNode jsonNode = new ObjectMapper().readTree(jsonResponse);

        WebClient.RequestHeadersUriSpec<?> uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
        when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(Mono.just(jsonNode));

        Mono<double[]> result = ipService.getCoordinates("8.8.8.8");

        StepVerifier.create(result)
                .assertNext(coords -> {
                    assert coords[0] == 40.7128;
                    assert coords[1] == -74.0060;
                })
                .verifyComplete();
    }

    @Test
    void getCoordinatesExternalIpError() {
        WebClient.RequestHeadersUriSpec<?> uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        doReturn(uriSpec).when(webClient).get();
        doReturn(headersSpec).when(uriSpec).uri(anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
        when(responseSpec.bodyToMono(JsonNode.class))
                .thenReturn(Mono.error(new RuntimeException("Connection refused")));

        Mono<double[]> result = ipService.getCoordinates("8.8.8.8");

        StepVerifier.create(result)
                .assertNext(coords -> {
                    assert coords[0] == 4.5709;
                    assert coords[1] == -74.2973;
                })
                .verifyComplete();
    }
}
