/**
 * 
 */
package hi.pico.engine.message;

public enum ShowLevel {
	ALERT("1"), STATUS("2");
	
	private String value;
	 
    private ShowLevel(String value) {
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