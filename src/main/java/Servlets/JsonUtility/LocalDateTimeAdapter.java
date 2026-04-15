package Servlets.JsonUtility;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;           
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, 
JsonDeserializer<LocalDateTime> {

private static final DateTimeFormatter FORMATTER = 
DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

public JsonElement serialize(LocalDateTime src, Type typeOfSrc, 
JsonSerializationContext context) {
return new JsonPrimitive(src.format(FORMATTER));
}

public LocalDateTime deserialize(JsonElement json, Type typeOfT, 
JsonDeserializationContext context) {
return LocalDateTime.parse(json.getAsString(), FORMATTER);
}
}