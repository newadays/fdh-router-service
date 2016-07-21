package com.ge.predix.solsvc.boot.spring;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.util.List;

import org.apache.http.Header;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.ge.predix.entity.field.Field;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.filter.Filter;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.router.boot.FdhRouterApplication;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * Spins up Spring Boot and accesses the URLs of the Rest apis
 * 
 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FdhRouterApplication.class)
@WebAppConfiguration
@IntegrationTest({ "server.port=0" })
public class TSDataIngestionControllerITTest {
	private static final Logger logger = LoggerFactory
			.getLogger(TSDataIngestionControllerITTest.class);

	private static final String TEST_FILE = "src/test/resources/sample-test.csv"; //$NON-NLS-1$

	@Value("${local.server.port}")
	private int localServerPort;

	private URL base;
	private RestTemplate template;

	@Autowired
	private RestClient restClient;

	@Autowired
	private JsonMapper jsonMapper;

	/**
	 * @throws Exception
	 *             -
	 */
	@Before
	public void setUp() throws Exception {
		this.setTemplate(new TestRestTemplate());

		this.base = new URL(
				"http://localhost:" + this.localServerPort + "/timeseries"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @throws Exception
	 *             -
	 */
	@SuppressWarnings({ "nls" })
	@Test
	public void getHello() throws Exception {
		ResponseEntity<String> response = this.template.getForEntity(
				this.base.toString(), String.class);
		assertThat(response.getBody(),
				startsWith("Greetings from Predix TS contoller"));
	}

	/**
	 * @throws Exception
	 *             -
	 */
	@SuppressWarnings({ "nls" })
	@Test
	public void getUploadCSV() throws Exception {
		List<Header> headers = this.restClient.getSecureTokenForClientId();
		MultiValueMap<String, String> multiHeaders = new LinkedMultiValueMap<String, String>();
		for(Header header:headers) {
			if(StringUtils.startsWithIgnoreCase(header.getName(),"authorization")){
				multiHeaders.add(header.getName(), header.getValue());
				break;
			}
		}
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", new FileSystemResource(TEST_FILE));
		String body = this.jsonMapper.toJson(createNewPutRequest());
		logger.info("payload to the upload "+body);
		parts.add("putfielddata", body);
		
		HttpEntity<?> httpEntity = new HttpEntity<Object>(parts , multiHeaders);
		
		

		String response = this.template.postForObject(this.base + "/upload",
				httpEntity, String.class);
		assertThat(response, startsWith("You uploaded"));

	}

	private PutFieldDataRequest createNewPutRequest() {
		PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
		PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();

		FieldData fieldData = new FieldData();
		Field field = new Field();
		FieldIdentifier fieldIdentifier = new FieldIdentifier();
		fieldIdentifier.setSource(FieldSourceEnum.PREDIX_TIMESERIES.name());
		field.setFieldIdentifier(fieldIdentifier);
		fieldData.setField(field);
		Filter selectionFilter = new Filter();
		fieldDataCriteria.setFilter(selectionFilter);
		putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);

		return putFieldDataRequest;
	}

	/**
	 * @return the template
	 */
	public RestTemplate getTemplate() {
		return this.template;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(RestTemplate template) {
		this.template = template;
	}
}