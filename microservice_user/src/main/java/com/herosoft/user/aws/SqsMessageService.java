package com.herosoft.user.aws;

import com.herosoft.user.po.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.model.*;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SqsMessageService {
    private final String queueName = "ChatMessageQueue.fifo";

    private SqsClient getClient() {
        return SqsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }

    public void processMessage(ChatMessage chatMessage){
        SqsClient sqsClient = getClient();

        try{
            MessageAttributeValue attributeValue = MessageAttributeValue.builder()
                    .stringValue(chatMessage.getName())
                    .dataType("String")
                    .build();

            Map<String,MessageAttributeValue> mapApp = new HashMap<>();
            mapApp.put("Name",attributeValue);

            GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();

            String queueUrl = sqsClient.getQueueUrl(getQueueUrlRequest).queueUrl();

            SendMessageRequest sendMessageReq = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageAttributes(mapApp)
                    .messageDeduplicationId(chatMessage.getId())
                    .messageGroupId("GroupChatA")
                    .messageBody(chatMessage.getMessage())
                    .build();

            sqsClient.sendMessage(sendMessageReq);

        }
        catch (SqsException e){
            log.info("发送SQS消息异常:{}",e.getLocalizedMessage());
        }
    }

    public List<ChatMessage> getMessages(){
        List<String> attr = new ArrayList<>();
        attr.add("Name");

        SqsClient sqsClient = getClient();

        GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();

        try{
            String queueUrl = sqsClient.getQueueUrl(getQueueUrlRequest).queueUrl();

            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageAttributeNames(attr)
                    .waitTimeSeconds(1)
                    .maxNumberOfMessages(10)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();

            List<ChatMessage> chatMessages = messages.stream().map(message ->{
                ChatMessage chatMessage = new ChatMessage();

                Map<String,MessageAttributeValue> mapAttr = message.messageAttributes();
                MessageAttributeValue attribute= mapAttr.get("Name");

                chatMessage.setName(attribute.stringValue());
                chatMessage.setId(message.messageId());
                chatMessage.setMessage(message.body());

                return chatMessage;
            }).collect(Collectors.toList());

            return chatMessages;
        }
        catch (SqsException e){
            log.info("获取SQS消息异常:{}",e.getLocalizedMessage());
        }

        return null;
    }

    public void purgeMessages(){
        SqsClient sqsClient = getClient();

        GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();

        PurgeQueueRequest purgeQueueRequest = PurgeQueueRequest.builder()
                .queueUrl(sqsClient.getQueueUrl(getQueueUrlRequest).queueUrl())
                .build();

        sqsClient.purgeQueue(purgeQueueRequest);
    }
}
