

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

```
	public static void main(String[] args) throws Exception {

		for (File file : FileUtils.listFiles(BASE, FileFilterUtils.suffixFileFilter(".java"),
		        FileFilterUtils.trueFileFilter())) {
			String className = file.getAbsolutePath().replace(BASE.getAbsolutePath(), "").replace(".java", "")
			        .replace(File.separator, ".").replaceFirst("^\\.", "");
			findTargets(className);
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<String> findTargets(String className) throws Exception {
		List<String> result = new ArrayList<String>();

		// check modifier
		Class clz = ClassUtils.getClass(className);
		if (Modifier.isAbstract(clz.getModifiers())) {
			return result;
		}

		// get all type annotations
		List<Annotation> annotations = new ArrayList<Annotation>(
		        Arrays.asList(clz.getAnnotationsByType(TestAnnotation1.class)));
		for (Class sclz : ClassUtils.getAllSuperclasses(clz)) {
			annotations.addAll(Arrays.asList(sclz.getAnnotationsByType(TestAnnotation1.class)));
		}
		if (annotations.isEmpty()) {
			return result;
		}

		// check
		for (Annotation annotation : annotations) {
			TestAnnotation1 target = (TestAnnotation1) annotation;
			String[] props = StringUtils.split(target.prop(), ".");
			Class tclz = clz;
			BeanWrapper wrapper;
			for (int i = 0; i < props.length; i++) {
				wrapper = new BeanWrapperImpl(tclz);
				tclz = wrapper.getPropertyType(props[i]);
			}
			System.out.println(target.prop() + ":" + tclz);
		}

		return result;
	}
```
