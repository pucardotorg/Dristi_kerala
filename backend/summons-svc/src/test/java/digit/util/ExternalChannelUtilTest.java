package digit.util;

import digit.channel.ChannelFactory;
import digit.channel.ExternalChannel;
import digit.web.models.ChannelMessage;
import digit.web.models.SummonsDelivery;
import digit.web.models.TaskRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalChannelUtilTest {

    @InjectMocks
    private ExternalChannelUtil externalChannelUtil;

    @Mock
    private TaskRequest request;

    @Mock
    private SummonsDelivery summonsDelivery;

    @Mock
    private ChannelFactory channelFactory;

    @Mock
    private ExternalChannel externalChannel;

    @Test
    void sendSummonsByDeliveryChannel() {
        // Arrange
        when(channelFactory.getDeliveryChannel(any())).thenReturn(externalChannel);
        when(externalChannel.sendSummons(any())).thenReturn(new ChannelMessage());
        // Act
        ChannelMessage channelMessage = externalChannelUtil.sendSummonsByDeliveryChannel(request, summonsDelivery);

        // Assert
        Assertions.assertNotNull(channelMessage);
    }
}
