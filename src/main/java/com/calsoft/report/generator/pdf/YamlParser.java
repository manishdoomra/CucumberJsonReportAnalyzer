package com.calsoft.report.generator.pdf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

public class YamlParser {
	
	/**
	 * Logger instance for this class
	 */
	
	public static Object load(String file) {
		try {
			return new Yaml().load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object load(InputStream file) {
		return new Yaml().load(file);
	}

	/**
	 * Load all YAML documents from the given file.
	 * 	Note: A file may contain several YAML documents, separated by "---".
	 * @param file Path to the file containing the YAML document(s).
	 * @return An Object per document.
	 */
	public static Iterable<Object> loadAll(String file) {
		try {
			return new Yaml().loadAll(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Iterable<Object> loadAll(InputStream stream) {
		return new Yaml().loadAll(stream);
	}

}
