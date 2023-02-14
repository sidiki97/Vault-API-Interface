package Models;

import com.google.gson.JsonObject;

public class DocFields extends Error{
    private String responseStatus;
    private JsonObject[] properties;
//    private JsonObject[] errors;

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Object[] getProperties() {
        return properties;
    }

    public void setProperties(JsonObject[] properties) {
        this.properties = properties;
    }

//    public JsonObject[] getErrors() {
//        return errors;
//    }
//
//    public void setErrors(JsonObject[] errors) {
//        this.errors = errors;
//    }
}
