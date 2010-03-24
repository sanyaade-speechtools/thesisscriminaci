package org.aitools.programd.util;

public class PluginSupportParam {
	
	private String name;
	private Class<?> paramClass;
	private String value;

	public PluginSupportParam(){
		this("", null, "");
	}
	
	public PluginSupportParam(String name, Class<?> paramClass, String value) {
		super();
		this.setName(name);
		this.setParamClass(paramClass);
		this.setValue(value);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setParamClass(Class<?> paramClass) {
		this.paramClass = paramClass;
	}

	public Class<?> getParamClass() {
		return paramClass;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}	
	
	@Override
	public String toString() {
		return "PluginSupportParam [name=" + name + ", paramClass="
				+ paramClass + ", value=" + value + "]";
	}

}
