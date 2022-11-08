package com.theodo.dojo.unittestdojo.pricer.yieldcurves;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class YieldCurveParser {

    public static class Curves extends HashMap<String, YieldCurve> {}
    public static class YieldCurveDeserializer extends StdDeserializer<Curves> {

        protected YieldCurveDeserializer() {
            super(Curves.class);
        }
        @Override
        public Curves deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            ArrayNode curvesNode =   (ArrayNode) node.get("curves");
            Curves outputs = new Curves();
            for (int i = 0; i < curvesNode.size(); i++) {
                JsonNode curveNode = curvesNode.get(i);
                String curveName = curveNode.get("name").asText();
                ArrayNode points = (ArrayNode) curveNode.get("points");
                YieldCurve yieldCurve = YieldCurve.builder().yieldCurveName(curveName).build();
                List<YieldCurve.CurvePoint> curvePoints = new ArrayList<>();
                for (int j = 0; j < points.size(); j++) {
                    JsonNode pointNode = points.get(j);
                    curvePoints.add(YieldCurve.CurvePoint.builder()
                            .yieldAtMaturity(pointNode.get("yield").asDouble())
                            .maturity(pointNode.get("maturity").asDouble())
                            .build());
                }
                yieldCurve.addPoints(curvePoints.toArray(new YieldCurve.CurvePoint[0]));
                outputs.put(yieldCurve.getYieldCurveName(), yieldCurve);
            }
            return outputs;
        }
    }

    public static Map<String, YieldCurve> readFile(String curveResourceName){
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Curves.class, new YieldCurveDeserializer());
        mapper.registerModule(module);

        try(InputStream resource = YieldCurveParser.class.getClassLoader().getResourceAsStream(curveResourceName)) {
            return mapper.readValue(resource, Curves.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
