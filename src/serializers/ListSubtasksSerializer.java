package serializers;

import com.google.gson.*;
import task.Subtask;

import java.lang.reflect.Type;
import java.util.List;

public class ListSubtasksSerializer implements JsonSerializer<List> {
    @Override
    public JsonElement serialize(List list, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray result = new JsonArray();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.registerTypeAdapter(Subtask.class, new SubtaskSerializer()).create();
        for (Object listElement : list) {
            result.add(gson.toJson(listElement, Subtask.class));
        }
        return result;
    }
}