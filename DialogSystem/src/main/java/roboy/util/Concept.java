package roboy.util;

import java.util.Map;
import java.util.HashMap;
import roboy.memory.RoboyMind;

import roboy.linguistics.Linguistics;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;

public class Concept {
	
	private Map<String, Object> attributes;

	public Concept()
	{
		this.attributes = new HashMap<String, Object>();
	}

	public Concept(Map<String, Object> attrs)
	{
		this.attributes = new HashMap<String, Object>();
		this.addAttributes(attrs);
		this.memorize((String)this.getAttribute("object_class"));
	}
	
	public Concept(String name){
		this();
		attributes.put(Linguistics.NAME, name);
		this.memorize("Person"); //TODO remove this constructor
	}

	public boolean addAttribute(String property, Object value)
	{
		this.attributes.put(property, value);
		return this.updateMemory();
	}

	public boolean addAttributes(Map<String, Object> attrs)
	{
		this.attributes.putAll(attrs);
		return this.updateMemory();
	}

	public Map<String, Object> getAttributes()
	{
		return this.attributes;
	}
	
	public Object getAttribute(String key){
		return attributes.get(key);
	}

	public String getProperties() 
	{
		String properties = ""; 

		for (Map.Entry<String, Object> attribute : this.getAttributes().entrySet())
		{
		    if (attribute.getKey() != "class" && attribute.getKey() != "id")
		    {
		    	properties +=  attribute.getKey() + ",";
		    }
		}

		if (properties.length()>0 && properties.charAt(properties.length()-1)==',')
		{
			properties = properties.substring(0, properties.length()-1);
		}

		return properties;

	}

	public String getValues() 
	{
		
		String values = "";

		for (Map.Entry<String, Object> attribute : this.getAttributes().entrySet())
		{
		    if (attribute.getKey() != "class" && attribute.getKey() != "id")
		    {
		    	values +=  attribute.getValue().toString() + ",";
		    }
		    
		}

		if (values.length()>0 && values.charAt(values.length()-1)==',')
		{
			values = values.substring(0, values.length()-1);
		}

		return values;
	}
	
	public boolean hasAttribute(String property)
	{
		if (this.getProperties() != "" && this.getProperties().contains(property))
		{
			return true;
		}
		return false;
	}

	public boolean memorize(String object_class)
	{
		return this.addAttribute("object_class", object_class);
	}

	public Object retrieve()
	{
		return RoboyMind.getInstance().retrieve(this);
	}

	public boolean updateMemory()
	{
		if (RoboyMind.getInstance().retrieve(this)==null)
		{
			RoboyMind.getInstance().save(this);
		}
		return RoboyMind.getInstance().update(this);
	}

	// TODO getClass
	// TODO getID

}
