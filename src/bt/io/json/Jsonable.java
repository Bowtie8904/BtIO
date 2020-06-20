package bt.io.json;

import org.json.JSONObject;

/**
 * AN interface which has to be implemented by all non-String objects in order for them to be correctly formatted by the
 * JSONBuilder.
 * 
 * @author &#8904
 *
 */
public interface Jsonable
{
    /**
     * This method is supposed to create a JSONObject which contains all fields and their values of the implementing
     * class.
     * 
     * <p>
     * This method is called by the JSONBuilder to correctly format the json.
     * </p>
     * 
     * @return The JSONObject.
     */
    public JSONObject toJSON();
}
