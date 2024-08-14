package digit.util;

import digit.config.Configuration;
import digit.web.models.TaskListResponse;
import digit.web.models.TaskRequest;
import digit.web.models.TaskResponse;
import digit.web.models.TaskSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static digit.config.ServiceConstants.TASK_SERVICE_ERROR;

@Component
@Slf4j
public class TaskUtil {

    private final RestTemplate restTemplate;

    private final Configuration config;

    public TaskUtil(RestTemplate restTemplate, Configuration config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    public TaskResponse callUpdateTask(TaskRequest taskRequest) {
         try {
            StringBuilder uri = new StringBuilder();
            uri.append(config.getTaskServiceHost())
                    .append(config.getTaskServiceUpdateEndpoint());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<TaskRequest> requestEntity = new HttpEntity<>(taskRequest, headers);

            ResponseEntity<TaskResponse> responseEntity = restTemplate.postForEntity(uri.toString(),
                    requestEntity, TaskResponse.class);

            return responseEntity.getBody();
        } catch (Exception e) {
            log.error(TASK_SERVICE_ERROR, e);
            throw new CustomException("TASK_UPDATE_ERROR", TASK_SERVICE_ERROR);
        }
    }

    public TaskListResponse callSearchTask(TaskSearchRequest searchRequest) {
        try {
            StringBuilder uri = new StringBuilder();
            uri.append(config.getTaskServiceHost())
                    .append(config.getTaskServiceUpdateEndpoint());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<TaskSearchRequest> requestEntity = new HttpEntity<>(searchRequest, headers);

            ResponseEntity<TaskListResponse> responseEntity = restTemplate.postForEntity(uri.toString(),
                    requestEntity, TaskListResponse.class);

            return responseEntity.getBody();
        } catch (Exception e) {
            log.error(TASK_SERVICE_ERROR, e);
            throw new CustomException("TASK_SEARCH_ERROR", TASK_SERVICE_ERROR);
        }
    }

    public TaskResponse callUploadDocumentTask(TaskRequest taskRequest) {
        try {
            StringBuilder uri = new StringBuilder();
            uri.append(config.getTaskServiceHost())
                    .append(config.getTaskServiceUpdateDocumentEndpoint());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<TaskRequest> requestEntity = new HttpEntity<>(taskRequest, headers);

            ResponseEntity<TaskResponse> responseEntity = restTemplate.postForEntity(uri.toString(),
                    requestEntity, TaskResponse.class);

            return responseEntity.getBody();
        } catch (Exception e) {
            log.error(TASK_SERVICE_ERROR, e);
            throw new CustomException("TASK_UPLOAD_DOCUMENT_ERROR", TASK_SERVICE_ERROR);
        }
    }
}
