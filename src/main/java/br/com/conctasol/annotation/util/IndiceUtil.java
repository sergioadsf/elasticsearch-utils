package br.com.conctasol.annotation.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import br.com.conctasol.annotation.Keyword;
import br.com.conctasol.annotation.MField;

public class IndiceUtil {
	

	public Map<String, Object> convert(Class<?> clazz) throws IOException {

		Map<String, Object> properties = new HashMap<String, Object>();

		criarFields(clazz, properties);

		Map<String, Object> mappingMap = new HashMap<String, Object>();
		mappingMap.put("properties", properties);

		return mappingMap;
	}

	private void criarFields(Class<?> controller, Map<String, Object> properties) {
		for (Field field : controller.getDeclaredFields()) {
			MField mapping = field.getAnnotation(MField.class);
			Map<String, Object> fieldMap = new HashMap<String, Object>();
			if (mapping != null) {
				String type = mapping.type();
				fieldMap.put("type", type);
				if ("keyword".equals(type)) {
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
	}

	private void criarKeyword(Field field, Map<String, Object> fieldMap, MField mapping) {
		Keyword keyword = field.getAnnotation(Keyword.class);
		if (keyword != null) {
			Map<String, Object> param = new HashMap<String, Object>();
			if (mapping != null) {
				Map<String, Object> kwmap = new HashMap<String, Object>();
				fieldMap.put("fields", kwmap);
				kwmap.put("keyword", param);
			}

			param.put("type", "keyword");
		}
	}
}
