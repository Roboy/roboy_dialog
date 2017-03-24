package roboy.util;

import java.util.Map;
import java.util.HashMap;

public class Concept {
	
	private Map<String, Object> attributes;

	public Concept()
	{
		this.attributes = new HashMap<String, Object>();
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

}
