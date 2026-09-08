package lk.ac.kln.unimart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("local")
class CorsSecurityTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void testPreflightRequestToListingsFromAllowedOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/listings")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5174")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Authorization,Content-Type"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5174"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsString("GET")))
                .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS));
    }

    @Test
    void testPreflightWithVariousBrowserHeaders() throws Exception {
        mockMvc.perform(options("/api/v1/listings")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5174")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization,content-type,accept,x-requested-with"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5174"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    void testGetListingsFromAllowedOrigin() throws Exception {
        mockMvc.perform(get("/api/v1/listings")
                        .param("status", "AVAILABLE")
                        .param("page", "0")
                        .param("size", "9")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5174"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5174"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testPreflightFromUnauthorizedOriginIsRejected() throws Exception {
        mockMvc.perform(options("/api/v1/listings")
                        .header(HttpHeaders.ORIGIN, "http://malicious-origin.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    void testProtectedEndpointStillRequiresAuthentication() throws Exception {
        // POST /api/v1/listings without bearer token must fail with 401 Unauthorized
        mockMvc.perform(post("/api/v1/listings")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5174")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test Listing\"}"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // DELETE /api/v1/listings/1 without bearer token must fail with 401 Unauthorized
        mockMvc.perform(delete("/api/v1/listings/1")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5174"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
