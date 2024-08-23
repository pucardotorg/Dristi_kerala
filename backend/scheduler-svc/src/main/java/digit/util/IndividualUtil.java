package digit.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import digit.repository.ServiceRequestRepository;
import digit.web.models.IndividualSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Slf4j
public class IndividualUtil {

    private final ServiceRequestRepository serviceRequestRepository;

    @Autowired
    public IndividualUtil(ServiceRequestRepository serviceRequestRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
    }


    public String getEmailByIndividualId(IndividualSearchRequest individualRequest, StringBuilder uri) {
        try{
            Object responseMap = serviceRequestRepository.fetchResult(uri, individualRequest);
            if(responseMap!=null){
                Gson gson= new Gson();
                String jsonString=gson.toJson(responseMap);
                log.info("Response :: {}", jsonString);
                JsonObject response = JsonParser.parseString(jsonString).getAsJsonObject();
                JsonArray individualObject=response.getAsJsonArray("Individual");
                if(!individualObject.isEmpty() && individualObject.get(0).getAsJsonObject().get("email") != null) {
                    return individualObject.get(0).getAsJsonObject().get("email").getAsString();
                }
                return null;
            }
            return null;
        }
        catch (CustomException e) {
            log.error("Custom Exception occurred in Individual Utility :: {}", e.toString());
            throw e;
        }
        catch (Exception e){
            throw new CustomException("INDIVIDUAL_UTILITY_EXCEPTION","Error in individual utility service: "+e.getMessage());
        }

    }
}