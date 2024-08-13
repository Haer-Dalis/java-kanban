package serializers;

import com.google.gson.*;
import task.Subtask;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListSubtasksDeserializer implements JsonDeserializer<List<Subtask>> {
    @Override
    public List<Subtask> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<Subtask> subtasks = new ArrayList<>();
        JsonArray jsonArray = json.getAsJsonArray();

        for (JsonElement element : jsonArray) {
            Subtask subtask = context.deserialize(element, Subtask.class);
            subtasks.add(subtask);
        }

        return subtasks;
    }
}