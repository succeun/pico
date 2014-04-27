package hi.pico;

public enum ArgumentType {
	ANY("ANY"), TEXT("TEXT"), XML("XML"), JSON("JSON"), HI_XML("HI_XML"), HI_JSON("HI_JSON");
	
	private String value;

    private ArgumentType(String value) {
            this.value = value;
    }
    
    public String value() {
    	return value;
    }
    
    @Override
    public String toString() {
    	return value;
    }
}
