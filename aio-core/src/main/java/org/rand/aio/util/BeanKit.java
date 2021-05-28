package org.rand.aio.util;

import org.rand.aio.util.anno.BeanAnnotaion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * 
 * @ClassName: BeanKit
 * @Description: 拷贝实体
 * @author rand
 * @date 2020年4月24日
 * @version V1.0
 */
public class BeanKit {
	private static final Logger log = LoggerFactory.getLogger(BeanKit.class);

	private static String GET = "get";

	private static String SET = "set";

	/**
	 * 
	 * @Method_Name: copyToTarget 
	 * @Description:  只支持常规实体，不支持多参数
	 * @param source 
	 * @param taget 
	 * @return T 返回类型 
	 * @date 2020年4月25日 
	 * @author rand 作者 
	 * @throws
	 */
	public static <E, T> T copyToTarget(E source, Class<T> target) {
		return newTargetInstance(source, target);
	}

	private static <E, T> T newTargetInstance(E source, Class<T> taget) {
		T newInstance = null;
		try {
			newInstance = taget.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		if (newInstance == null) {
			return null;
		}
		Class<? extends Object> newclazz = newInstance.getClass();
		HashMap<FieldEntity, String> sourceMap = getSoureceMap(source);
		HashMap<FieldEntity, String> tagertMap = getTagertMap(newInstance);
		for (FieldEntity sourceEntity : sourceMap.keySet()) {
			String sourceAnno = sourceEntity.getAnno();
			String sourceName = sourceEntity.getName();
			Object sourceValue = sourceEntity.getValue();
			String sourceType = sourceMap.get(sourceEntity);
			for (FieldEntity targetEntity : tagertMap.keySet()) {
				String targetAnno = targetEntity.getAnno();
				String targetName = targetEntity.getName();
				String targetType = tagertMap.get(targetEntity);
				if (StringKit.isEmpty(sourceType) || !sourceType.equals(targetType)) {
					continue;
				}
				if (sourceAnno != null) {
					if (sourceAnno.equals(targetAnno) || sourceAnno.equals(targetName)) {
						setTargetInstance(newInstance, newclazz, targetName, sourceValue);
					}
				} else if (targetAnno != null) {
					if (targetAnno.equals(sourceAnno) || targetAnno.equals(sourceName)) {
						setTargetInstance(newInstance, newclazz, targetName, sourceValue);
					}
				} else if (StringKit.isNotEmpty(sourceName) && sourceName.equals(targetName)) {
					setTargetInstance(newInstance, newclazz, targetName, sourceValue);
				}
			}
		}
		return newInstance;
	}

	private static <T> void setTargetInstance(T newInstance, Class<? extends Object> newclazz, String name,
			Object sourceValue) {
		String simplemethod = name.substring(0, 1).toUpperCase() + name.substring(1);
		String setmethodname = SET + simplemethod;
		try {
			Field field = newclazz.getDeclaredField(name);
			Method setmethod = newclazz.getDeclaredMethod(setmethodname, field.getType());
			setmethod.invoke(newInstance, sourceValue);
		} catch (NoSuchFieldException e) {
			log.error(e.toString());
		} catch (SecurityException e) {
			log.error(e.toString());
		} catch (NoSuchMethodException e) {
			log.error(e.toString());
		} catch (IllegalAccessException e) {
			log.error(e.toString());
		} catch (IllegalArgumentException e) {
			log.error(e.toString());
		} catch (InvocationTargetException e) {
			log.error(e.toString());
		}
	}
	
	private static <E> HashMap<FieldEntity, String> getSoureceMap(E source) {
		return getAttr(source);
	}
	

	private static <T> HashMap<FieldEntity, String> getTagertMap(T taget) {
		return getAttr(taget);
	}
	
	private static HashMap<FieldEntity, String> getAttr(Object obj){
		HashMap<FieldEntity, String> hashMap = new HashMap<FieldEntity, String>();
		Class<? extends Object> clazz = obj.getClass();
		Field[] declaredFields = clazz.getDeclaredFields();
		for (Field field : declaredFields) {
			FieldEntity entity = new FieldEntity();
			String type = field.getType().toString();
			String name = field.getName();
			// 获取属性注解的值
			Annotation[] annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof BeanAnnotaion) {
					entity.setAnno(((BeanAnnotaion) annotation).value());
				}
			}
			// 获取属性名
			entity.setName(name);
			// 获取属性值
			String simplemethod = name.substring(0, 1).toUpperCase() + name.substring(1);
			String getmethodname = GET + simplemethod;
			Object invoke;
			try {
				Method getmethod = clazz.getDeclaredMethod(getmethodname);
				invoke = getmethod.invoke(obj);
				entity.setValue(invoke);
				hashMap.put(entity, type);
			} catch (NoSuchMethodException e) {
				log.error(e.toString());
			} catch (SecurityException e) {
				log.error(e.toString());
			} catch (IllegalAccessException e) {
				log.error(e.toString());
			} catch (IllegalArgumentException e) {
				log.error(e.toString());
			} catch (InvocationTargetException e) {
				log.error(e.toString());
			}
		}
		return hashMap;
	}
}
