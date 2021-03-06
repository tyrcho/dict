package com.tyrcho.dictionary.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import com.tyrcho.dictionary.Utils;

public class Question {
	private String inputTranslation;

	private DictionaryEntry dictionaryEntry;

	private String word;

	private final String ignoredChars;

	public Question(String word, DictionaryEntry dictionaryEntry,
			String ignoredChars) {
		this.ignoredChars = ignoredChars;
		setWord(word);
		setDictionaryEntry(dictionaryEntry);
	}

	// Redefinition de la methode de l'objet Object
	public String toString() {
		return "Donnez la traduction de : " + word;
	}

	// Ajout de la traduction fourni par l'utilisateur
	public void setInputTranslation(String inputTranslation) {
		this.inputTranslation = inputTranslation;
	}

	// Test si la traduction proposee par l'utilisateur est egale e une des
	// traductions possibles
	public boolean isAnswerValid() {
		for (String translation : dictionaryEntry.translations()) {
			if (Utils.simpleCompare(translation, inputTranslation, ignoredChars)) {
				return true;
			}
		}
		return false;
	}

	// Retourne la ou les traductions possibles
	public String getTranslation() {
		return dictionaryEntry.translations().toString();
	}

	public void setDictionaryEntry(DictionaryEntry dictionaryEntry) {
		this.dictionaryEntry = dictionaryEntry;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public DictionaryEntry dictionnaryEntry() {
		return dictionaryEntry;
	}

	public String getWord() {
		return word;
	}
}
