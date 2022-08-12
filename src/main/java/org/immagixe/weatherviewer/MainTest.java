package org.immagixe.weatherviewer;

import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

public class MainTest {
    public static void main(String[] args) {


        StringBuilder urlencoded = new StringBuilder("https://api.openweathermap.org/data/2.5?q=");
        urlencoded.append("?username=");


        urlencoded.append(URLEncoder.encode("john@1.com", StandardCharsets.UTF_8));
        urlencoded.append("&password=");
        urlencoded.append(URLEncoder.encode("P@$$word", StandardCharsets.UTF_8));
        urlencoded.append("&phone=");
        urlencoded.append(URLEncoder.encode("&911", StandardCharsets.UTF_8));
        URI uri = URI.create(String.valueOf(urlencoded));


        String q = "random word £500 bank $";
        String url = "https://example.com?q=" + URLEncoder.encode(q, StandardCharsets.UTF_8);

        System.out.println(uri);


        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("https://api.openweathermap.org/data/2.5?q={host}");
        factory.setDefaultUriVariables(singletonMap("host", "Казань"));

        URI uri2 = factory.uriString("/weather").build();

        System.out.println(uri2.toString());


    }
}

//        public void encodeTemplateAndValues() {
//            DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
//            factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
//            UriBuilder uriBuilder = factory.uriString("/hotel list/{city} specials?q={value}");
//            String expected = "/hotel%20list/Z%C3%BCrich%20specials?q=a%2Bb";
//            Map<String, Object> vars = new HashMap<>();
//            vars.put("city", "Z\u00fcrich");
//            vars.put("value", "a+b");
//            assertEquals(expected, uriBuilder.build("Z\u00fcrich", "a+b").toString());
//            assertEquals(expected, uriBuilder.build(vars).toString());
//        }
//
//    }
//
//    private static void assertEquals(String expected, String toString) {
//    }



