package urlshortener2015.common.web;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static urlshortener2015.common.web.fixture.ShortURLFixture.someUrl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import urlshortener2015.common.domain.ShortURL;
import urlshortener2015.common.repository.ClickRepository;
import urlshortener2015.common.repository.ShortURLRepository;
import urlshortener2015.common.web.UrlShortenerController;

public class UrlShortenerTests {

	private MockMvc mockMvc;

	@Mock
	private ShortURLRepository shortURLRepository;

	@Mock
	private ClickRepository clickRespository;

	@InjectMocks
	private UrlShortenerController urlShortener;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
	}

	@Test
	public void thatRedirectToReturnsTemporaryRedirectIfKeyExists()
			throws Exception {
		when(shortURLRepository.findByKey("someKey")).thenReturn(someUrl());

		mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().isTemporaryRedirect())
				.andExpect(redirectedUrl("http://example.com/"));
	}

	@Test
	public void thatRedirecToReturnsNotFoundIdIfKeyDoesNotExist()
			throws Exception {
		when(shortURLRepository.findByKey("someKey")).thenReturn(null);

		mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void thatShortenerCreatesARedirectIfTheURLisOK() throws Exception {
		configureTransparentSave();

		mockMvc.perform(post("/link").param("url", "http://example.com/"))
				.andDo(print())
				.andExpect(redirectedUrl("http://localhost/f684a3c4"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.hash", is("f684a3c4")))
				.andExpect(jsonPath("$.uri", is("http://localhost/f684a3c4")))
				.andExpect(jsonPath("$.target", is("http://example.com/")))
				.andExpect(jsonPath("$.sponsor", is(nullValue())));
	}

	@Test
	public void thatShortenerCreatesARedirectWithSponsor() throws Exception {
		configureTransparentSave();

		mockMvc.perform(
				post("/link").param("url", "http://example.com/").param(
						"sponsor", "http://sponsor.com/")).andDo(print())
				.andExpect(redirectedUrl("http://localhost/f684a3c4"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.hash", is("f684a3c4")))
				.andExpect(jsonPath("$.uri", is("http://localhost/f684a3c4")))
				.andExpect(jsonPath("$.target", is("http://example.com/")))
				.andExpect(jsonPath("$.sponsor", is("http://sponsor.com/")));
	}

	@Test
	public void thatShortenerFailsIfTheURLisWrong() throws Exception {
		configureTransparentSave();

		mockMvc.perform(post("/link").param("url", "someKey")).andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void thatShortenerFailsIfTheRepositoryReturnsNull() throws Exception {
		when(shortURLRepository.save(org.mockito.Matchers.any(ShortURL.class)))
				.thenReturn(null);

		mockMvc.perform(post("/link").param("url", "someKey")).andDo(print())
				.andExpect(status().isBadRequest());
	}

	private void configureTransparentSave() {
		when(shortURLRepository.save(org.mockito.Matchers.any(ShortURL.class)))
				.then(new Answer<ShortURL>() {
					@Override
					public ShortURL answer(InvocationOnMock invocation)
							throws Throwable {
						return (ShortURL) invocation.getArguments()[0];
					}
				});
	}
}
