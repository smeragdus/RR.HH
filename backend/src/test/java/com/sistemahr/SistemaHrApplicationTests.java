package com.sistemahr;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SistemaHrApplicationTests {
	private final MockMvc mvc;

	@Autowired
	SistemaHrApplicationTests(MockMvc mvc) {
		this.mvc = mvc;
	}

	@Test
	void contextLoads() {
	}

	@Test
	void servesFrontendEntryPoints() throws Exception {
		mvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<div id=\"root\"></div>")));
		mvc.perform(get("/index.html"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<div id=\"root\"></div>")));
	}

}
