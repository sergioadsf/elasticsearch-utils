package br.com.conctasol.annotation.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import br.com.conctasol.annotation.Keyword;
import br.com.conctasol.annotation.MField;
import br.com.conctasol.annotation.MSplitField;

public class IndiceUtil {

	private static final String KEYWORD = "keyword";

	public Map<String, Object> convert(Class<?> clazz) {

		Map<String, Object> properties = new HashMap<>();

		criarFields(clazz, properties);

		HashMap<String, Object> mappingMap = new HashMap<>();
		mappingMap.put("properties", properties);

		return mappingMap;
	}

	public String convertJson(Class<?> clazz) {

		StringBuilder sb = new StringBuilder("{");
		sb.append("\"_doc\": {");
		sb.append("\"properties\": {");
		this.criarFields(clazz, sb);
		
		sb.setLength(sb.length() - 1);
		sb.append("}");
		sb.append("}");
		sb.append("}");
		return sb.toString();
	}

	private void criarFields(Class<?> controller, StringBuilder sb) {
		for (Field field : controller.getDeclaredFields()) {
			MSplitField splitField = field.getAnnotation(MSplitField.class);
			if (splitField != null) {
				for (MField mField : splitField.value()) {
					this.doCreateField(sb, field, mField);
				}
			} else {
				MField mapping = field.getAnnotation(MField.class);
				this.doCreateField(sb, field, mapping);
			}
		}
	}

	private void doCreateField(StringBuilder sb, Field field, MField mapping) {
		if (mapping != null) {
			sb.append("\"").append(mapping.name()).append("\": {");
			String type = mapping.type();
			sb.append("\"type\":").append("\"").append(type).append("\"");
			if (KEYWORD.equals(type)) {
				sb.append(",\"index\":").append("\"").append(mapping.index()).append("\"");
			}
			if ("text".equals(type)) {
				criarKeyword(field, sb, mapping);
			}
			String format = mapping.format();
			if (!"".equals(format)) {
				sb.append("{\"format\":").append("\"").append(mapping.format()).append("\"}");
			}
			sb.append("},");
		} else {
			criarKeyword(field, sb, null);
		}
	}

	private void criarKeyword(Field field, StringBuilder sb, MField mapping) {
		Keyword keyword = field.getAnnotation(Keyword.class);
		if (keyword != null && mapping != null) {
			sb.append(",\"fields\":  ");
			sb.append("{\"keyword\": ");
			sb.append("{\"type\":").append("\"").append(KEYWORD).append("\"}");
			sb.append("}");

		}
	}

	private void criarFields(Class<?> controller, Map<String, Object> properties) {
		for (Field field : controller.getDeclaredFields()) {
			MSplitField splitField = field.getAnnotation(MSplitField.class);
			if (splitField != null) {
				for (MField mField : splitField.value()) {
					this.doCreateField(properties, field, mField);
				}
			} else {
				MField mapping = field.getAnnotation(MField.class);
				this.doCreateField(properties, field, mapping);
			}
		}
	}

	private void doCreateField(Map<String, Object> properties, Field field, MField mapping) {
		Map<String, Object> fieldMap = new HashMap<>();
		if (mapping != null) {
			String type = mapping.type();
			fieldMap.put("type", type);
			if (KEYWORD.equals(type)) {
				fieldMap.put("index", mapping.index());
			}
			if ("text".equals(type)) {
				criarKeyword(field, fieldMap, mapping);
			}
			String format = mapping.format();
			if (!"".equals(format)) {
				fieldMap.put("format", mapping.format());
			}

			properties.put(mapping.name(), fieldMap);
		} else {
			criarKeyword(field, fieldMap, null);
		}
	}

	private void criarKeyword(Field field, Map<String, Object> fieldMap, MField mapping) {
		Keyword keyword = field.getAnnotation(Keyword.class);
		if (keyword != null) {
			Map<String, Object> param = new HashMap<>();
			if (mapping != null) {
				Map<String, Object> kwmap = new HashMap<>();
				fieldMap.put("fields", kwmap);
				kwmap.put(KEYWORD, param);
			}

			param.put("type", KEYWORD);
		}
	}
}
