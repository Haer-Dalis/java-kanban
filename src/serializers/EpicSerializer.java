package serializers;

import com.google.gson.*;
import task.Epic;

import java.lang.reflect.Type;


public class EpicSerializer implements JsonSerializer<Epic> {
    @Override
    public JsonElement serialize(Epic epic, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        if (epic.getId() != 0) {
            result.addProperty("id", epic.getId());
        }


        result.addProperty("nameOfTask", epic.getName());
        result.addProperty("descriptionOfTask", epic.getDescription());
        JsonArray subtasks = new JsonArray();

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        for (Integer i : epic.getSubtasksOfEpic()) {
            subtasks.add(gson.toJson(i));
        }
        result.add("subtasksInEpic", subtasks);

        return result;
    }

}
