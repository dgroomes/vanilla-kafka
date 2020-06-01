import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dgroomes.kafkaplayground.springheaders.model.MessageA;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExploratoryTest {

    public static void main(String[] args) throws JsonProcessingException {
        new ExploratoryTest().doit();
    }

    @Test
    void doit() throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        var json = "{\n" +
                "  \"message\": \"hello(55)\",\n" +
                "  \"type\": \"dgroomes.kafkaplayground.springheaders.model.MessageA\",\n" +
                "  \"a\": \"A for Apple\"\n" +
                "}\n";

        var obj = objectMapper.readValue(json, MessageA.class);

        assertEquals("A for Apple", obj.a);
    }
}
