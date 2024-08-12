package serializers;

import com.google.gson.*;
import task.Epic;

import java.lang.reflect.Type;

public class EpicDeserializer implements JsonDeserializer<Epic> {
    @Override
    public Epic deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        int id = 0;

        if (jsonObject.has("id")) {
            id = jsonObject.get("id").getAsInt();
        }

        String nameOfTask = jsonObject.get("nameOfTask").getAsString();
        String descriptionOfTask = jsonObject.get("descriptionOfTask").getAsString();

        Epic epic = new Epic(nameOfTask, descriptionOfTask);
        if (id != 0) {
            epic.setId(id);
        }
        return epic;
    }
}
