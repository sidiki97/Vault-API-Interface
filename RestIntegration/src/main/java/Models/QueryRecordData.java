package Models;

import com.google.gson.JsonObject;

public class QueryRecordData {
    private JsonObject[] data;

    public JsonObject[] getData() {
        return data;
    }

    public void setData(JsonObject[] data) {
        this.data = data;
    }
}
