package digit.service;

import digit.config.Configuration;
import digit.util.IndividualUtil;
import digit.web.models.IndividualSearch;
import digit.web.models.IndividualSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@Slf4j
public class IndividualService {

    private final IndividualUtil individualUtils;
    private final Configuration config;

    @Autowired
    public IndividualService(IndividualUtil individualUtils, Configuration config) {
        this.individualUtils = individualUtils;
        this.config = config;
    }

    public String getEmailId(RequestInfo requestInfo , String individualId){
        try {
            IndividualSearchRequest individualSearchRequest = new IndividualSearchRequest();
            individualSearchRequest.setRequestInfo(requestInfo);
            IndividualSearch individualSearch = new IndividualSearch();
            log.info("Individual Id :: {}", individualId);
            individualSearch.setIndividualId(individualId);
            individualSearchRequest.setIndividual(individualSearch);
            StringBuilder uri = new StringBuilder(config.getIndividualHost()).append(config.getIndividualSearchEndpoint());
            uri.append("?limit=1000").append("&offset=0").append("&tenantId=").append(requestInfo.getUserInfo().getTenantId()).append("&includeDeleted=true");
            return individualUtils.getEmailByIndividualId(individualSearchRequest, uri);
        }
        catch (Exception e){
            log.error("Error in search individual service :: {}", e.toString());
            log.error("Email Not found");
            return null;
        }
    }
}