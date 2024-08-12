package serializers;

import com.google.gson.*;
import task.Subtask;

import java.lang.reflect.Type;
import java.util.HashMap;

public class HashMapSubtasksSerializer implements JsonSerializer<HashMap> {
    @Override
    public JsonElement serialize(HashMap hashMap, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray result = new JsonArray();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.registerTypeAdapter(Subtask.class, new SubtaskSerializer()).create();
        for (Object key : hashMap.keySet()) {
            result.add(gson.toJson(hashMap.get(key), Subtask.class));
        }
        return result;
    }
}