package Models;

import com.google.gson.JsonObject;

public abstract class Error {
    private JsonObject[] errors;

    public JsonObject[] getErrors() {
        return errors;
    }

    public void setErrors(JsonObject[] errors) {
        this.errors = errors;
    }
}
