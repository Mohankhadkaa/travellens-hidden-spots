package com.example.travellens.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void travellerCanViewPostAfterCreatingIt() throws Exception {
        String email = "traveller-test@example.com";
        String password = "secret123";

        mockMvc.perform(post("/signup")
                        .param("username", "travellertest")
                        .param("email", email)
                        .param("password", password)
                        .param("confirmPassword", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        MvcResult loginResult = mockMvc.perform(post("/login")
                        .param("username", email)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        MvcResult createResult = mockMvc.perform(post("/posts")
                        .session(session)
                        .param("title", "Traveller Test Spot")
                        .param("location", "Pokhara")
                        .param("category", "Nature")
                        .param("description", "A peaceful lakeside place."))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String detailUrl = createResult.getResponse().getRedirectedUrl();

        mockMvc.perform(get(detailUrl).session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Traveller Test Spot")))
                .andExpect(content().string(containsString("Edit")))
                .andExpect(content().string(containsString("Delete")));
    }

    @Test
    void adminCanOpenTravellerPostFromAdminPanel() throws Exception {
        String email = "admin-panel-traveller@example.com";
        String password = "secret123";

        mockMvc.perform(post("/signup")
                        .param("username", "adminpaneltraveller")
                        .param("email", email)
                        .param("password", password)
                        .param("confirmPassword", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        MvcResult travellerLoginResult = mockMvc.perform(post("/login")
                        .param("username", email)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpSession travellerSession = (MockHttpSession) travellerLoginResult.getRequest().getSession(false);

        MvcResult createResult = mockMvc.perform(post("/posts")
                        .session(travellerSession)
                        .param("title", "Admin Visible Traveller Spot")
                        .param("location", "Kathmandu")
                        .param("category", "Culture")
                        .param("description", "A hidden spot admins should be able to inspect."))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String detailUrl = createResult.getResponse().getRedirectedUrl();

        MvcResult adminLoginResult = mockMvc.perform(post("/login")
                        .param("username", "admin@travellens.com")
                        .param("password", "AdminPass123!"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpSession adminSession = (MockHttpSession) adminLoginResult.getRequest().getSession(false);

        mockMvc.perform(get("/admin/posts").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Admin Visible Traveller Spot")))
                .andExpect(content().string(containsString(detailUrl)));

        mockMvc.perform(get(detailUrl).session(adminSession))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Admin Visible Traveller Spot")))
                .andExpect(content().string(containsString("Edit")))
                .andExpect(content().string(containsString("Delete")));
    }
}
