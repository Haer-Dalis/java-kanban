package serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import task.Subtask;

import java.lang.reflect.Type;

public class SubtaskSerializer implements JsonSerializer<Subtask> {
    @Override
    public JsonElement serialize(Subtask subtask, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        if (subtask.getId() != 0) {
            result.addProperty("id", subtask.getId());
        }



        result.addProperty("epicId", subtask.getEpicId());
        result.addProperty("nameOfTask", subtask.getName());
        result.addProperty("descriptionOfTask", subtask.getDescription());
        result.addProperty("statusOfTask", subtask.getStatus().toString());
        result.addProperty("year", subtask.getStartTime().getYear());
        result.addProperty("month", subtask.getStartTime().getMonthValue());
        result.addProperty("day", subtask.getStartTime().getDayOfMonth());
        result.addProperty("hour", subtask.getStartTime().getHour());
        result.addProperty("minute", subtask.getStartTime().getMinute());
        result.addProperty("duration", subtask.getDuration().getSeconds());

        return result;
    }

}