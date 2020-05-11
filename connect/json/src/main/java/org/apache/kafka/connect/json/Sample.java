/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Time;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Sample {

    public void write(Map<String, Object> configs, int numRecords) {
        SchemaBuilder flatBuilder = SchemaBuilder.struct()
                .field("byte", Schema.INT8_SCHEMA)
                .field("int", Schema.INT32_SCHEMA)
                .field("string", Schema.STRING_SCHEMA)
                .field("double", Schema.FLOAT64_SCHEMA)
                .field("boolean", Schema.BOOLEAN_SCHEMA)
                .field("date", Date.SCHEMA)
                .field("time", Time.SCHEMA);

        Schema flat = flatBuilder.build();

        SchemaBuilder nestedBuilder = SchemaBuilder.struct()
                .field("string", Schema.STRING_SCHEMA)
                .field("nested", flatBuilder.schema());

        Schema secondNest = SchemaBuilder.struct()
                .field("string", Schema.STRING_SCHEMA)
                .field("nested", nestedBuilder.build())
                .build();

        GregorianCalendar calendar = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.DATE, 10000);

        Struct f = new Struct(flat)
                .put("byte", (byte) 10)
                .put("int", 12)
                .put("string", "asd")
                .put("double", 3.1415)
                .put("boolean", true)
                .put("date", calendar.getTime())
                .put("time", new java.util.Date(14400000));

        Struct struct = new Struct(secondNest)
                .put("string", "one")
                .put("nested",
                        new Struct(nestedBuilder.build())
                                .put("string", "two")
                                .put("nested", f)
                );

        System.out.println(struct);

        JsonConverter converter = new JsonConverter();
        Map<String, String> props = new HashMap<>();
        props.put("schemas.enable", "false");
        props.put("converter.type", "value");
        converter.configure(props);

        System.out.println(new String(converter.fromConnectData("t", secondNest, struct)));
    }

    public static void main(String[] args) throws Exception {
        List<Integer> l = Arrays.asList(1, 5, 8, 3, 8, 2, 9, 0);
        l.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1 > o2) return -1;
                else if (o2 > o1) return 1;
                return 0;
            }
        });

        System.out.println(l);

        String json = "{'a': 10, 'b': {'c': 10}}".replace('\'', '"');
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> t = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        System.out.println(t);

        Sample sample = new Sample();
        sample.write(null, 10);
    }
}
