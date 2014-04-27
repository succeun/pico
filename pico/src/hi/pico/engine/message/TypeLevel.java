/**
 * 
 */
package hi.pico.engine.message;

public enum TypeLevel {
	FATAL("FATAL"), ERROR("ERROR"), WARN("WARN"), INFO("INFO");
	
	private String value;

    private TypeLevel(String value) {
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