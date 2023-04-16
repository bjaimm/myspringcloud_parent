package com.herosoft.user.aws;

import com.herosoft.user.po.Greeting;
import com.herosoft.user.po.GreetingItems;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

@Service
@Slf4j
public class DynamoDBEnhanced {

    @Autowired
    private Environment environment;

    public void insertDynamoDB(Greeting greeting){
        Region region = Region.US_EAST_1;

        log.info("当前环境变量AWS_ACCESS_KEY_ID:{}",environment.getProperty("AWS_ACCESS_KEY_ID"));
        log.info("当前环境变量AWS_SECRET_ACCESS_KEY:{}",environment.getProperty("AWS_SECRET_ACCESS_KEY"));

        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        try {
            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(ddb)
                    .build();


            DynamoDbTable<GreetingItems> dynamoDbTable = enhancedClient.table("Greeting", TableSchema.fromBean(GreetingItems.class));

            GreetingItems gi = new GreetingItems();

            gi.setName(greeting.getName());
            gi.setId(greeting.getId());
            gi.setMessage(greeting.getBody());
            gi.setTitle(greeting.getTitle());

            PutItemEnhancedRequest putItemEnhancedRequest = PutItemEnhancedRequest.builder(GreetingItems.class)
                    .item(gi)
                    .build();

            dynamoDbTable.putItem(putItemEnhancedRequest);

        }
        catch (DynamoDbException ex){
            log.info("创建Dynamodb table发生异常:{}",ex.getLocalizedMessage());
        }

    }
}
