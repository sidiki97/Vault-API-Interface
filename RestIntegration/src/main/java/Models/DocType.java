package Models;

import com.google.gson.JsonObject;

public class DocType {
    private JsonObject[] types;

    public JsonObject[] getTypes() {
        return types;
    }

    public void setTypes(JsonObject[] types) {
        this.types = types;
    }
}
