package com.theodo.dojo.unittestdojo.pricer.cashflows;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.theodo.dojo.unittestdojo.utils.DateUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashFlowParser {

    public static class CashFlowsByName extends HashMap<String, CashFlows> {}
    public static class CashFlowsDeserializer extends StdDeserializer<CashFlowsByName> {

        protected CashFlowsDeserializer() {
            super(CashFlowsByName.class);
        }

        @Override
        public CashFlowsByName deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            ArrayNode cashFlowsNode =   (ArrayNode) node.get("cashFlows");
            CashFlowsByName outputs = new CashFlowsByName();
            for (int i = 0; i < cashFlowsNode.size(); i++) {
                JsonNode cashFlowNode = cashFlowsNode.get(i);
                String isin = cashFlowNode.get("isin").asText();
                ArrayNode amounts = (ArrayNode) cashFlowNode.get("amounts");
                CashFlows cashFlows = CashFlows.builder().build();
                List<CashFlows.CashFlow> values = new ArrayList<>();
                for (int j = 0; j < amounts.size(); j++) {
                    JsonNode pointNode = amounts.get(j);
                    values.add(CashFlows.CashFlow.builder()
                                    .cashFlowValue(pointNode.get("value").asDouble())
                                    .date(DateUtils.parseDate(pointNode.get("date").asText()))
                            .build());
                }
                cashFlows.addCashFlow(values.toArray(new CashFlows.CashFlow[0]));
                outputs.put(isin, cashFlows);
            }
            return outputs;
        }
    }

    public static Map<String, CashFlows> readFile(String curveResourceName){
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());

        SimpleModule module = new SimpleModule();
        module.addDeserializer(CashFlowsByName.class, new CashFlowsDeserializer());
        mapper.registerModule(module);

        try(InputStream resource = CashFlowParser.class.getClassLoader().getResourceAsStream(curveResourceName)) {
            return mapper.readValue(resource, CashFlowsByName.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
