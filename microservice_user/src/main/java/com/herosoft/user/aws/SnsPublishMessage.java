package com.herosoft.user.aws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SnsException;

@Service
@Slf4j
public class SnsPublishMessage {
    public void publishMessage(String id){

        Region region = Region.US_EAST_1;

        SnsClient snsClient = SnsClient.builder()
                .region(region)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        String message = "A new item which has id value of "+id+" was added in DynamoDB table Greeting";

        try {
            PublishRequest publishRequest = PublishRequest.builder()
                    .message(message)
                    .targetArn("arn:aws:sns:us-east-1:930504375187:dynamodb-data-change-notification")
                    .build();

            snsClient.publish(publishRequest);
        }
        catch (SnsException e){
            log.info("发送SNS异常:{}",e.getLocalizedMessage());
        }

    }
}
