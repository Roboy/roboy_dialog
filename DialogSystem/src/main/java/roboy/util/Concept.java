package roboy.util;

import java.util.Map;

import roboy.linguistics.Linguistics;

import java.util.HashMap;

public class Concept {
	
	private Map<String, Object> attributes;

	public Concept()
	{
		this.attributes = new HashMap<String, Object>();
	}
	
	public Concept(String name){
		this();
		attributes.put(Linguistics.NAME, name);
	}

	public void addAttribute(String property, Object value)
	{
		this.attributes.put(property, value);	
	}

	public void addAttributes(Map<String, Object> attrs)
	{
		this.attributes.putAll(attrs);
	}

	public Map<String, Object> getAttributes()
	{
		return this.attributes;
	}
	
	public Object getAttribute(String key){
		return attributes.get(key);
	}

}
