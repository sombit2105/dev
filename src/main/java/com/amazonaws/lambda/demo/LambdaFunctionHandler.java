package com.amazonaws.lambda.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.GetSMSAttributesRequest;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SetSMSAttributesRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;

public class LambdaFunctionHandler implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object input, Context context) {
    	
    	context.getLogger().log("Input: " + input);
    	
    	String phoneNumber = "919901530041";
    	//String phoneNumber = "919916684932";
    	String message =  "Hi..";

    	AmazonSNSClient snsClient = new AmazonSNSClient();
    	//String topicArn = createSNSTopic(snsClient);
    	
    	Map<String, MessageAttributeValue> smsAttributes = setSMSAttributes();

    	sendSMSMessage(snsClient, message, phoneNumber, smsAttributes);
        return "Sentit";
    }
/*    @Override
    public String handleRequest(Object input, Context context) {
    	
    	context.getLogger().log("Input: " + input);
    	
    	input = "919901530041,919844007741";
    	String message =  "Hi..Bring horlicks and dryfruits!!";

    	AmazonSNSClient snsClient = new AmazonSNSClient();
    	String topicArn = createSNSTopic(snsClient);
    	
    	Map<String, MessageAttributeValue> smsAttributes = setSMSAttributes();

    	StringTokenizer tokenizer = new StringTokenizer((String) input, ","); 
    	while (tokenizer.hasMoreTokens())
    	{ 
    		String phoneNumber = tokenizer.nextToken();
    		subscribeToTopic(snsClient, topicArn, "sms", phoneNumber);
    		sendSMSMessage(snsClient, message, phoneNumber, smsAttributes);
    	} 
        return "Sentit";
    }*/

	public Map<String, MessageAttributeValue> setSMSAttributes() {
		Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<String, MessageAttributeValue>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("mySenderID") //The sender ID shown on the device.
                .withDataType("String"));
        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                .withStringValue("1.50") //Sets the max price to 0.50 USD.
                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Promotional") //Sets the type to promotional.
                .withDataType("String"));
        return smsAttributes;
	}
    
    public static String createSNSTopic(AmazonSNSClient snsClient) {
        CreateTopicRequest createTopic = new CreateTopicRequest("mySNSTopic");
        CreateTopicResult result = snsClient.createTopic(createTopic);
        System.out.println("Create topic request: " + 
            snsClient.getCachedResponseMetadata(createTopic));
        System.out.println("Create topic result: " + result);
        return result.getTopicArn();
    }
    
    public static void subscribeToTopic(AmazonSNSClient snsClient, String topicArn, 
    		String protocol, String endpoint) {	
            SubscribeRequest subscribe = new SubscribeRequest(topicArn, protocol,
                                                              endpoint);
            SubscribeResult subscribeResult = snsClient.subscribe(subscribe);
            System.out.println("Subscribe request: " + 
                    snsClient.getCachedResponseMetadata(subscribe));
            System.out.println("Subscribe result: " + subscribeResult);
    }

    public static void sendSMSMessage(AmazonSNSClient snsClient, String message, 
    		String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
            PublishResult result = snsClient.publish(new PublishRequest()
                            .withMessage(message)
                            .withPhoneNumber(phoneNumber)
                            .withMessageAttributes(smsAttributes));
            System.out.println(result); // Prints the message ID.
    }



}
