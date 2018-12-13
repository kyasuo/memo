

```
	private static List<String> findMistakedFields(String className) throws ClassNotFoundException {
		List<String> result = new ArrayList<String>();
		for (Field field : FieldUtils.getAllFieldsList(ClassUtils.getClass(className))) {
			Class type = field.getType();
			if (Modifier.isStatic(field.getModifiers()) || type.isPrimitive() || type == String.class
			        || type == Integer.class) { // TODO
				continue;
			}
			List<Annotation> annotations = Arrays.asList(field.getAnnotations());
			if (!CollectionUtils.exists(annotations, getPredicateForValidation())) {
				continue;
			}
			if (!CollectionUtils.exists(annotations, getPredicateForValid())) {
				result.add(field.getName());
			}
		}
		return result;
	}

	private static Predicate getPredicateForValidation() {
		return new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				String name = ((Annotation) object).annotationType().getSimpleName();
				return name.startsWith("Depre"); // TODO
			}
		};
	}

	private static Predicate getPredicateForValid() {
		return new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				String name = ((Annotation) object).annotationType().getSimpleName();
				return "Valid".equals(name); // TODO
			}
		};
	}
```
