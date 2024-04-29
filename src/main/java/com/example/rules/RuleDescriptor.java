package com.example.rules;

public record RuleDescriptor(String id, String body, ReturnType returnType, CollectionType collectionType) {

  enum ReturnType {
    STRING,
    INTEGER,
    LONG,
    DOUBLE,
    BOOLEAN,
    DATE,
    DATETIME,
    TIME
  }

  enum CollectionType {
    LIST,
    MAP
  }

}
