package com.hfsolution.app.services;


import org.springframework.data.jpa.domain.Specification;

import com.hfsolution.app.util.AppTools;

import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.util.*;

public class CustomSpecification<T> implements Specification<T> {

    private final Map<String, String> queryMap;
    @SuppressWarnings("rawtypes")
    private final Map<String, Class<? extends Enum>> enumFields;

    public CustomSpecification(String queryString, @SuppressWarnings("rawtypes") Map<String, Class<? extends Enum>> enumFields) {
        this.queryMap = parseQuery(queryString);
        this.enumFields = enumFields;
    }
    public CustomSpecification(String queryString) {
        this.queryMap = parseQuery(queryString);
        this.enumFields = new HashMap<>();
    }

    private Map<String, String> parseQuery(String queryString) {
        Map<String, String> map = new HashMap<>();
       if(queryString!=null&&!queryString.isEmpty()){
        String[] queries = queryString.split(",");
        for (String query : queries) {
            String[] keyValue = query.split("=", 2);
            if (keyValue.length == 2) {
                map.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
       }
        return map;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        for (Map.Entry<String, String> entry : queryMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Check if field is an ENUM type
            if (enumFields.containsKey(key)) {
                predicates.add(handleEnumField(root, criteriaBuilder, key, value));
            } else {
                predicates.add(handleStandardField(root, criteriaBuilder, key, value));
            }
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Predicate handleEnumField(Root<T> root, CriteriaBuilder criteriaBuilder, String key, String value) {
        Class<? extends Enum> enumClass = enumFields.get(key);
        Enum enumValue = Enum.valueOf(enumClass, value.toUpperCase());

        return criteriaBuilder.equal(root.get(key), enumValue);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Predicate handleStandardField(Root<T> root, CriteriaBuilder criteriaBuilder, String key, String value) {
        String[] nestedKeys = key.split("\\.");

        // Handle nested properties using join
        Path<?> path = root;
        for (int i = 0; i < nestedKeys.length - 1; i++) {
            path = ((From<?, ?>) path).join(nestedKeys[i]);
        }
        String field = nestedKeys[nestedKeys.length - 1];

        // The rest of the logic remains similar, but using `path.get(field)` instead of `root.get(key)`
        if (value.matches("^\\[.*~.*\\]$")) {
            // Range match: k=[min~max]
            String[] range = value.substring(1, value.length() - 1).split("~");
            if (range.length == 2) {
                Comparable min = (Comparable) parseValue(range[0].trim());
                Comparable max = (Comparable) parseValue(range[1].trim());
                Expression<? extends Comparable> nestedPath = path.get(field);
                return criteriaBuilder.between(nestedPath, min, max);
            }
        } else if (value.matches("^\\{.*\\}$")) {
            // Union list: k={v1 v2 v3}
            String[] values = value.substring(1, value.length() - 1).split(" ");
            return path.get(field).in(Arrays.asList(values));
        } else if (value.matches("^\\(.*\\)$")) {
            // Intersection list: k=(v1 v2 v3)
            String[] values = value.substring(1, value.length() - 1).split(" ");
            List<Predicate> orPredicates = new ArrayList<>();
            for (String val : values) {
                orPredicates.add(criteriaBuilder.equal(path.get(field), parseValue(val)));
            }
            return criteriaBuilder.and(orPredicates.toArray(new Predicate[0]));
        } else if (value.startsWith("~")) {
            // Fuzzy match: k=~v
            return criteriaBuilder.like(path.get(field), "%" + value.substring(1) + "%");
        } else {
            // Exact match: k=v
            return criteriaBuilder.equal(path.get(field), parseValue(value));
        }

        return null;
    }


    private Object parseValue(String value) {
    // Attempt to parse as Integer
    try {
        return Integer.parseInt(value);
    } catch (NumberFormatException e1) {
        // Attempt to parse as BigDecimal
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e2) {
            // Attempt to parse as LocalDateTime
            try {
                return AppTools.formatDateStringToTimestamp(value,"yyyy-MM-dd HH:mm:ss.SSS");
            } catch (Exception e3) {
                // Handle strings with quotes
                return value.replace("\"", "").replace("'", "");
            }
        }
    }
}

  
}
