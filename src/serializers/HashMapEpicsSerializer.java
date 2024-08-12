package serializers;

import com.google.gson.*;
import task.Epic;

import java.lang.reflect.Type;
import java.util.HashMap;

public class HashMapEpicsSerializer implements JsonSerializer<HashMap> {
    @Override
    public JsonElement serialize(HashMap hashMap, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray result = new JsonArray();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.registerTypeAdapter(Epic.class, new EpicSerializer()).create();
        for (Object key : hashMap.keySet()) {
            result.add(gson.toJson(hashMap.get(key), Epic.class));
        }
        return result;
    }
}