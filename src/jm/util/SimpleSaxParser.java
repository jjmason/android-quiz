package jm.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Xml;

public class SimpleSaxParser {
	public interface StartTagHandler {
		void handleStartTag(SimpleSaxParser parser, String name,
				Attributes attrs) throws XmlException;
	}

	public interface EndTagHandler {
		void handleEndTag(SimpleSaxParser parser, String name)
				throws XmlException;
	}

	private static final StartTagHandler dummyStartTagHandler = new StartTagHandler() {
		@Override
		public void handleStartTag(SimpleSaxParser parser, String name,
				Attributes attrs) { /* empty */
		}
	};

	private static final EndTagHandler dummyEndTagHandler = new EndTagHandler() {
		@Override
		public void handleEndTag(SimpleSaxParser parser, String name) {/* empty */
		}
	};

	private final StringBuilder buffer = new StringBuilder();
	private final Map<String, StartTagHandler> startTagHandlers = new HashMap<String, StartTagHandler>();
	private final Map<String, EndTagHandler> endTagHandlers = new HashMap<String, EndTagHandler>();
	private StartTagHandler defaultStartTagHandler = dummyStartTagHandler;
	private EndTagHandler defaultEndTagHandler = dummyEndTagHandler;

	public void setStartTagHandler(String name, StartTagHandler handler) {
		if (name == null)
			throw new NullPointerException("name");
		if (handler == null)
			handler = dummyStartTagHandler;
		startTagHandlers.put(name, handler);
	}

	public void setEndTagHandler(String name, EndTagHandler handler) {
		if (name == null)
			throw new NullPointerException("name");
		if (handler == null)
			handler = dummyEndTagHandler;
		endTagHandlers.put(name, handler);
	}

	public void setDefaultStartTagHandler(StartTagHandler handler) {
		defaultStartTagHandler = handler == null ? dummyStartTagHandler
				: handler;
	}

	public void setDefaultEndTagHandler(EndTagHandler handler) {
		defaultEndTagHandler = handler == null ? dummyEndTagHandler : handler;
	}
	
	public String getBuffer(){
		return buffer.toString().trim();
	}
	
	public String takeBuffer(){
		String s = getBuffer();
		clearBuffer();
		return s;
	}
	
	public void clearBuffer(){
		buffer.delete(0, buffer.length());
	}
	
	public void parse(Reader source) throws XmlException {
		try {
			Xml.parse(source, new Handler());
		} catch (SAXException e) {
			throw unwindException(e);
		} catch (IOException e) {
			throw new XmlException(e);
		}
	}
	
	public void parse(InputStream in) throws XmlException {
		try{
			Xml.parse(in, null, new Handler());
		}catch(SAXException e){
			throw unwindException(e);
		}catch(IOException e){
			throw new XmlException(e);
		}
	}

	private StartTagHandler getStartTagHandler(String name) {
		StartTagHandler h = startTagHandlers.get(name);
		if (h == null)
			h = defaultStartTagHandler;
		return h;
	}

	private EndTagHandler getEndTagHandler(String name) {
		EndTagHandler h = endTagHandlers.get(name);
		if (h == null)
			h = defaultEndTagHandler;
		return h;
	}

	private static XmlException unwindException(Exception e){
		XmlException found = null;
		Throwable current = e;
		while(current != null){
			if(current instanceof XmlException){
				found = (XmlException) current;
				break;
			}
			if(current.getCause() == null || current == current.getCause()){
				break;
			}
			current = current.getCause();
		}
		return found == null ? new XmlException(e) : found;
	}
	
	private class Handler extends DefaultHandler {
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			buffer.append(ch, start, length);
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			try {
				getStartTagHandler(localName).handleStartTag(SimpleSaxParser.this, 
						localName, attributes);
			} catch (XmlException e) {
				throw new SAXException(e);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			try {
				getEndTagHandler(localName).handleEndTag(SimpleSaxParser.this, localName);
			} catch (XmlException e) {
				throw new SAXException(e);
			}
		}
	}
}
