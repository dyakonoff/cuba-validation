package io.dyakonoff.controllersvalidation.service;


public interface BadWordsDetectionService {
    String NAME = "controllersvalidation_BadWordsDetectionService";

    String detectBadWords(String text);
}