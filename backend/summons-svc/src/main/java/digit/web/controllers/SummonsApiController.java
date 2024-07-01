package digit.web.controllers;


import digit.kafka.Producer;
import digit.service.SummonsService;
import digit.util.ResponseInfoFactory;
import digit.web.models.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-05-29T13:38:04.562296+05:30[Asia/Calcutta]")
@Controller
@RequestMapping
public class SummonsApiController {

    private final SummonsService summonsService;

    private final ResponseInfoFactory responseInfoFactory;

    private final Producer producer;

    @Autowired
    public SummonsApiController(SummonsService summonsService, ResponseInfoFactory responseInfoFactory, Producer producer) {
        this.summonsService = summonsService;
        this.responseInfoFactory = responseInfoFactory;
        this.producer = producer;
    }

    @RequestMapping(value = "summons/v1/_generateSummons", method = RequestMethod.POST)
    public ResponseEntity<GenerateSummonResponse> generateSummons(@Parameter(in = ParameterIn.DEFAULT, description = "Details for generating a summon.", required = true, schema = @Schema()) @Valid @RequestBody GenerateSummonsRequest request) {
        SummonsDocument summonsDocument = null;
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
        GenerateSummonResponse response = GenerateSummonResponse.builder().summonsDocument(summonsDocument).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @RequestMapping(value = "summons/v1/_push", method = RequestMethod.POST)
    public ResponseEntity<?>  testSmsPush(@RequestBody Object object) {
        producer.push("egov.core.notification.sms", object);
        return new ResponseEntity<>(null,HttpStatus.OK);
    }

    @RequestMapping(value = "summons/v1/_sendSummons", method = RequestMethod.POST)
    public ResponseEntity<SummonsResponse> sendSummons(@Parameter(in = ParameterIn.DEFAULT, description = "Details for the Sending Summons + RequestInfo meta data.", required = true, schema = @Schema()) @Valid @RequestBody SendSummonsRequest request) {
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
        SummonsDelivery summonsDelivery = summonsService.sendSummonsViaChannels(request);
        SummonsResponse response = SummonsResponse.builder().summonsDelivery(summonsDelivery).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @RequestMapping(value = "summons/v1/_updateSummons", method = RequestMethod.POST)
    public ResponseEntity<UpdateSummonsResponse> updateSummonsStatus(@Parameter(in = ParameterIn.DEFAULT, description = "Details for the Updating Summons + RequestInfo meta data.", required = true, schema = @Schema()) @Valid @RequestBody UpdateSummonsRequest request) {
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
        ChannelMessage channelMessage = summonsService.updateSummonsDeliveryStatus(request);
        UpdateSummonsResponse response = UpdateSummonsResponse.builder().channelMessage(channelMessage).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

}
