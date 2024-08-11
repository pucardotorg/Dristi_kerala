package digit.channel;

import digit.kafka.Producer;
import digit.service.EmailService;
import digit.web.models.ChannelMessage;
import digit.web.models.TaskRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailChannel implements ExternalChannel{

    private final Producer producer;
    private final EmailService emailService;

    public EmailChannel(Producer producer, EmailService emailService) {
        this.producer = producer;
        this.emailService = emailService;
    }

    @Override
    public ChannelMessage sendSummons(TaskRequest request) {
        emailService.sendEmail(request);
        return ChannelMessage.builder().acknowledgementStatus("success").build();
    }
}
