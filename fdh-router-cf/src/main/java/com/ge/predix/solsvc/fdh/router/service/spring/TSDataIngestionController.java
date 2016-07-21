package com.ge.predix.solsvc.fdh.router.service.spring;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ge.predix.entity.datafile.DataFile;
import com.ge.predix.entity.field.Field;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.filter.Filter;
import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.router.service.PutFieldDataService;

/**
 * An example of creating a Rest api using Spring Annotations @RestController.
 * 
 * 
 * 
 * @author predix
 */
@RestController
public class TSDataIngestionController {
	private static final Logger logger = LoggerFactory
			.getLogger(TSDataIngestionController.class);
	
	@Autowired
	private
	PutFieldDataService putFieldDataService;
	
	@Autowired
	private JsonMapper jsonMapper;

	/**
     * 
     */
	public TSDataIngestionController() {
		super();
	}

	
	/**
	 * Sample End point which returns a Welcome Message
	 * 
	 * @param echo
	 *            - the string to echo back
	 * @return -
	 */
	@SuppressWarnings("nls")
	@RequestMapping("/timeseries")
	public String index(
			@RequestParam(value = "echo", defaultValue = "echo") String echo) {
		return "Greetings from Predix TS contoller " + (new Date());
	}

	/**
	 * Upload a csv file to timeseries
	 * 
	 * @param name -
	 * @param file -
	 * @param authorization -
	 * @param putFieldData -
	 * @param putFieldDataRequest -
	 * @return -
	 */
	@RequestMapping(value = "/timeseries/upload", method = RequestMethod.POST)
	public @ResponseBody String handleFileUpload(
			@RequestParam("file") MultipartFile file,@RequestHeader(value="authorization") String authorization , @RequestParam("putfielddata") String putFieldData) {

		String name = ""; //$NON-NLS-1$
		String statusMessage = ""; //$NON-NLS-1$

		try {

			if (file.isEmpty()) {
				statusMessage = "You failed uploaded " + name //$NON-NLS-1$
						+ " because the file was empty"; //$NON-NLS-1$
				return statusMessage;
			}
			
			PutFieldDataRequest putFieldDataRequest = this.jsonMapper.fromJson(putFieldData, PutFieldDataRequest.class);
			updatePutRequest(file,putFieldDataRequest)   ;  
			//PutFieldDataRequest putFieldDataRequest =  createNewPutRequest(file);
			
			Map<Integer, Model> modelLookupMap= new HashMap<Integer, Model>();
			List<Header> headers = new ArrayList<Header>();
			headers.add(new BasicHeader("authorization", authorization)); //$NON-NLS-1$
			
			getPutFieldDataService().putFieldData(putFieldDataRequest, modelLookupMap, headers);
			
			statusMessage = "You uploaded " + file.getOriginalFilename() ;//$NON-NLS-1$
			

		} catch (Exception e) {
			logger.error("Error Uploading Timeseries data", e); //$NON-NLS-1$
			return "You failed uploaded " + name //$NON-NLS-1$
					+ " because the following error" + e.getMessage(); //$NON-NLS-1$
		}

		return statusMessage;
	}

	
	private void updatePutRequest(MultipartFile file, PutFieldDataRequest putFieldDataRequest) {
		
		 if ( putFieldDataRequest == null)
	            throw new RuntimeException("PutFieldDataRequest is missing "); //$NON-NLS-1$
		 
		PutFieldDataCriteria fieldDataCriteria = null;
		if(putFieldDataRequest !=null && (putFieldDataRequest.getPutFieldDataCriteria() == null ||  putFieldDataRequest.getPutFieldDataCriteria().size()== 0))
		{
			fieldDataCriteria = new PutFieldDataCriteria() ;
			Filter selectionFilter = new Filter();
			fieldDataCriteria.setFilter(selectionFilter);
			putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);
		}
		fieldDataCriteria = putFieldDataRequest.getPutFieldDataCriteria().get(0);
		FieldData fieldData = new FieldData();
		Field field = new Field();
		FieldIdentifier  fieldIdentifier = new FieldIdentifier();
		fieldIdentifier.setSource(FieldSourceEnum.PREDIX_TIMESERIES.name());
		field.setFieldIdentifier(fieldIdentifier);
		fieldData.setField(field);
		DataFile datafile = new DataFile();
		datafile.setFile(file);
		fieldData.setData(datafile);
		fieldDataCriteria.setFieldData(fieldData);
	}


	/**
	 * @return the putFieldDataService
	 */
	public PutFieldDataService getPutFieldDataService() {
		return this.putFieldDataService;
	}

	/**
	 * @param putFieldDataService the putFieldDataService to set
	 */
	public void setPutFieldDataService(PutFieldDataService putFieldDataService) {
		this.putFieldDataService = putFieldDataService;
	}

}