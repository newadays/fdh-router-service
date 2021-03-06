package com.ge.predix.solsvc.fdh.router.service;

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
public interface GetFieldDataService
{

    /**
     * @param uri -
     * @param headers -
     * @param request -
     * @return -
     */
    GetFieldDataResult getFieldData(GetFieldDataRequest request, Map<Integer, Model> modelLookupMap, List<Header> headers );


}
