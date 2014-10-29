package org.opentosca.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Message;
import org.w3c.dom.Node;

import org.eclipse.winery.highlevelrestapi.HighLevelRestApi;

public abstract class AbstractIAService {

	@Resource
	private WebServiceContext context;

	protected void sendResponse (HashMap<String,String> returnParameters) {
	
		// Extract message
		WrappedMessageContext wrappedContext = (WrappedMessageContext) context.getMessageContext();
		Message message = wrappedContext.getWrappedMessage();
		
		// Extract headers from message
		List<Header> headers = CastUtils.cast((List<?>) message.get(Header.HEADER_LIST));
		
		// Find ReplyTo and MessageID SOAP Header
		String replyTo = null;
		String messageID = null;
		for (Header iter : headers) {
						
			Object headerObject = iter.getObject();

			// Unmarshall to org.w3c.dom.Node
			if (headerObject instanceof Node) {
				Node node = (Node) headerObject;
				String localPart = iter.getName().getLocalPart();
				String content = node.getTextContent();
				
				// Extract ReplyTo Header value
				if ("ReplyTo".equals(localPart)) {
					replyTo = content;
				}
				
				// Extract MessageID Header value
				if ("MessageID".equals(localPart)) {
					messageID = content;
				}
			}						
		}
		
		// Create asynchronous SOAP Response Message
		StringBuilder builder = new StringBuilder();
		
		builder.append("<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:sch='http://siserver.org/schema'>");
		builder.append("   <soapenv:Header/>");
		builder.append("   <soapenv:Body>");
		builder.append("      <sch:invokeResponse>");
		builder.append("         <sch:MessageID>" + messageID + "</sch:MessageID>");

		// Insert return parameters into asynchronous SOAP Response Message
		for (Entry<String, String> paramIter : returnParameters.entrySet()) {
			
			String key = paramIter.getKey();
			String value = paramIter.getValue();
			
			builder.append("         <" + key + ">" + value + "</" + key +">");
		}

		builder.append("      </sch:invokeResponse>");			
		builder.append("	</soapenv:Body>");
		builder.append("</soapenv:Envelope>");
		
		// Send SOAP Response Message back to requester
		if(replyTo == null) {
			System.err.println("No 'ReplyTo' header found!\nTherefore, reply message is printed here:\n" + builder.toString());
		} else {
			HighLevelRestApi.Post(replyTo, builder.toString(), "");
		}
	}

	protected void sendFaultResponse (String faultcode, String faultstring) {
		
		// Extract message
		WrappedMessageContext wrappedContext = (WrappedMessageContext) context.getMessageContext();
		Message message = wrappedContext.getWrappedMessage();
		
		//System.out.println("SOAP wrappedContext"+wrappedContext.isEmpty());
		
		// Extract headers from message
		List<Header> headers = CastUtils.cast((List<?>) message.get(Header.HEADER_LIST));
		
		// Find ReplyTo and MessageID SOAP Header
		String replyTo = null;
		String messageID = null;
		for (Header iter : headers) {
						
			Object headerObject = iter.getObject();

			// Unmarshall to org.w3c.dom.Node
			if (headerObject instanceof Node) {
				Node node = (Node) headerObject;
				String localPart = iter.getName().getLocalPart();
				String content = node.getTextContent();
				
				// Extract ReplyTo Header value
				if ("ReplyTo".equals(localPart)) {
					replyTo = content;
				}
				
				// Extract MessageID Header value
				if ("MessageID".equals(localPart)) {
					messageID = content;
				}
			}						
		}
		
		// Create asynchronous SOAP Response Message
		StringBuilder builder = new StringBuilder();
		
		builder.append("<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>");
		builder.append("   <soapenv:Header/>");
		builder.append("   <soapenv:Body>");
		builder.append("   	<soapenv:Fault>");		

		// Insert return parameters into asynchronous SOAP Response Message
			
		builder.append("       <faultcode>" + faultcode + "</faultcode>");
		builder.append("       <faultstring>" + faultstring + "</faultstring>");	
		
		builder.append("     </soapenv:Fault>");
		builder.append("	</soapenv:Body>");
		builder.append("</soapenv:Envelope>");
		
		// Send SOAP Response Message back to requester
		if(replyTo == null) {
			System.err.println("No 'ReplyTo' header found!\nTherefore, reply message is printed here:\n" + builder.toString());
		} else 
		{			
			HighLevelRestApi.Post(replyTo, builder.toString(), "");
		}
	}

}