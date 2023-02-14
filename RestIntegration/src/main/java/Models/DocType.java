package Models;

import com.google.gson.JsonObject;

public class DocType {
    private String responseStatus;
    private JsonObject[] types;
    private JsonObject[] errors;

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public JsonObject[] getTypes() {
        return types;
    }

    public void setTypes(JsonObject[] types) {
        this.types = types;
    }

    public JsonObject[] getErrors() {
        return errors;
    }

    public void setErrors(JsonObject[] errors) {
        this.errors = errors;
    }
}
