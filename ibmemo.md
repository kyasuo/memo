
## ibatis
* com.ibatis.sqlmap.engine.accessplan.PropertyAccessPlan#setProperties

```
		for(Field field : FieldUtils.getAllFields(input.getClass())) {
			if (!Modifier.isStatic(field.getModifiers())) {
				field.setAccessible(true);
				paramMap.put(field.getName(), field.get(input));
			}
		}
```
