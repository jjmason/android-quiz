package jm.util;

public class XmlException extends Exception {
	private static final long serialVersionUID = 1L;

	private String file;
	private int line = -1;
	
	public XmlException() {
		super(); 
	}
	
	public XmlException(String message, Throwable cause) {
		super(message, cause); 
	}

	public XmlException(String message) {
		super(message); 
	}

	public XmlException(Throwable cause) {
		super(cause); 
	}

	public XmlException(String message, String file, int line){
		super(message);
		this.file = file;
		this.line = line;
	}
	
	public XmlException(String message, String file, int line, Throwable cause){
		super(message, cause);
		this.file = file;
		this.line = line;
	}
	
	public XmlException(String message, String file){
		this(message, file, -1);
	}
	
	public XmlException(String message, String file, Throwable cause){
		this(message,file,-1,cause);
	}

	
	public String getFile() {
		return file;
	}

	public int getLine() {
		return line;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("XmlException");
		if(file != null)
			sb.append(" in ").append(file);
		if(line != -1)
			sb.append(" at line ").append(line);
		sb.append(": ").append(getMessage());
		return sb.toString();
	}
	
}
