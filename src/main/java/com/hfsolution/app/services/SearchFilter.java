package com.hfsolution.app.services;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.hfsolution.app.dto.SearchRequest;
import com.hfsolution.app.enums.GlobalOperator;
import com.hfsolution.app.util.AppTools;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class SearchFilter<T> {
    public Specification<T> getSearchSpecification(SearchRequest searchRequest){
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb){
                return cb.equal(root.get(searchRequest.getColumn()), searchRequest.getValue());
            }
        };
    }

    public Specification<T> getSearchSpecification(List<SearchRequest> searchRequests, GlobalOperator globalOperator){
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb){
                if (searchRequests == null || searchRequests.isEmpty()) {
                    // Returning null means no condition, so all records will be returned
                    return null;
                }
                List<Predicate> predicates =  new ArrayList<>();
                for (SearchRequest searchRequest : searchRequests) {
                    
                    switch (searchRequest.getOperator()) {
                        case EQUAL:
                            Predicate equal = cb.equal(root.get(searchRequest.getColumn()), AppTools.convertValue(searchRequest.getValue(), searchRequest.getFieldType()));
                            predicates.add(equal);
                            break;
                        case LIKE:
                            Predicate like = cb.like(root.get(searchRequest.getColumn()), "%"+searchRequest.getValue()+"%");
                            predicates.add(like);
                            break;
                        case NOT_EQUAL:
                            Predicate notEqual = cb.notEqual(root.get(searchRequest.getColumn()), searchRequest.getValue());
                            predicates.add(notEqual);
                            break;
                        case GREATER_THAN:
                            Predicate greaterThan = cb.greaterThan(root.get(searchRequest.getColumn()), searchRequest.getValue());
                            predicates.add(greaterThan);
                            break;
                        case LESS_THAN:
                            Predicate lessThan = cb.lessThan(root.get(searchRequest.getColumn()), searchRequest.getValue());
                            predicates.add(lessThan);
                            break;
                        case IN:
                            String[] split = searchRequest.getValue().split(",");
                            Predicate in = root.get(searchRequest.getColumn()).in(Arrays.asList(split));
                            predicates.add(in);
                            break;
                        case BETWEEN:
                            String[] split2 = searchRequest.getValue().split(",");
                            Predicate between = cb.between(root.get(searchRequest.getColumn()), split2[0], split2[1]);
                            predicates.add(between);
                            break;
                        case JOIN:
                            Predicate join = cb.equal(root.join(searchRequest.getJoinTable()).get(searchRequest.getColumn()), searchRequest.getValue());
                            predicates.add(join);
                            break;
                        default:
                            throw new IllegalStateException("Unexception value");
                    }
                }
                if(GlobalOperator.AND.equals(globalOperator)){
                    return cb.and(predicates.toArray(new Predicate[0]));
                }else {
                    return cb.or(predicates.toArray(new Predicate[0]));
                }
                
            }
        };
    }
    
}
