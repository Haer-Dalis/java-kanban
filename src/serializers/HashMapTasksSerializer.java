package serializers;

import com.google.gson.*;
import task.Task;

import java.lang.reflect.Type;
import java.util.HashMap;

public class HashMapTasksSerializer implements JsonSerializer<HashMap> {
    @Override
    public JsonElement serialize(HashMap hashMap, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray result = new JsonArray();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.registerTypeAdapter(Task.class, new TaskSerializer()).create();
        for (Object key : hashMap.keySet()) {
            result.add(gson.toJson(hashMap.get(key), Task.class));
        }
        return result;
    }
}

