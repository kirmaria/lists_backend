package com.thekirschners.lists.utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    private static final String SPLIT_CHAR = ",";
    private static final String LEFT_DELIMITER = "[";
    private static final String RIGHT_DELIMITER = "]";

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        return stringList == null ? "" : stringList.stream().map(s -> LEFT_DELIMITER + s + RIGHT_DELIMITER).collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {
        return (string == null || string.isBlank()) ? new ArrayList<>() : Arrays.stream(string.split(SPLIT_CHAR)).map(s -> s.substring(1, s.length()-1)).collect(Collectors.toList());
    }
}
