package Models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocFields {
    private JsonObject[] properties;


    public Object[] getProperties() {
        return properties;
    }

    public void setProperties(JsonObject[] properties) {
        this.properties = properties;
    }


}
