package com.ge.predix.solsvc.fdh.handler;

import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;

/**
 * 
 * @author predix -
 */
public interface GetFieldDataHandler
{

    /**
     * @param uri -
     * @param headers -
     * @param request -
     * @param modelLookupMap -
     * @param httpMethod - GET, PUT, POST, DELETE
     * @return -
     */
    GetFieldDataResult getFieldData(GetFieldDataRequest request, Map<Integer, Model> modelLookupMap, List<Header> headers, String httpMethod );


}
