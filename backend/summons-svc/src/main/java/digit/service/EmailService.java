package digit.service;

import digit.config.Configuration;
import digit.kafka.Producer;
import digit.web.models.Email;
import digit.web.models.EmailRequest;
import digit.web.models.TaskRequest;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import static digit.config.ServiceConstants.TASK_EMAIL_TEMPLATE;

@Service
@Slf4j
public class EmailService {

    private final Producer producer;
    private final Configuration config;

    @Autowired
    public EmailService(Producer producer, Configuration config) {
        this.producer = producer;
        this.config = config;
    }

    public void sendEmail(TaskRequest request) {
        EmailRequest emailRequest = getEmailRequestFromTask(request);
        producer.push(config.getEmailNotificationTopic(), emailRequest);
    }

    private EmailRequest getEmailRequestFromTask(TaskRequest request) {
        EmailRequest emailRequest = new EmailRequest();
        RequestInfo requestInfo = request.getRequestInfo();
        Email email = new Email();

        email.setHTML(true);
        email.setBody("{\"taskNumber\":" + request.getTask().getTaskNumber() + "}");
        email.setTemplateCode(TASK_EMAIL_TEMPLATE);
        email.setEmailTo(Set.of(request.getTask().getTaskDetails().getRespondentDetails().getEmail()));
        email.setSubject("Task Assigned");

        emailRequest.setRequestInfo(requestInfo);
        emailRequest.setEmail(email);

        return emailRequest;
    }
}