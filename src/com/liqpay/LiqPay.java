package com.liqpay;


import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class LiqPay {
	
	
	private String host = "https://www.liqpay.com/api/";	
	private String pub_key = "";
	private String priv_key = "";	
	
	
	
	public LiqPay(String public_key, String private_key){
		pub_key = public_key;
		priv_key = private_key;
	}
	
	
	
	public LiqPay(String public_key, String private_key, String url){
		pub_key = public_key;
		priv_key = private_key;
		host = url;
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> api(String path, HashMap<String, String> list) throws Exception{
		
		JSONObject json = new JSONObject();
		
		json.put("public_key", pub_key);
		
		for (Map.Entry<String, String> entry: list.entrySet())
			json.put(entry.getKey(), entry.getValue());
		
		String dataJson = json.toString();
		String signature = LiqPayUtil.base64_encode( LiqPayUtil.sha1( priv_key + dataJson + priv_key) );
		
		HashMap<String, String> data = new HashMap<String, String>(); 
		data.put("data", dataJson);
		data.put("signature", signature);
		String resp = LiqPayRequest.post(host + path, data);
		
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(resp);
		JSONObject jsonObj = (JSONObject) obj;
		
		HashMap<String, Object> res_json = LiqPayUtil.parseJson(jsonObj);
		
		return res_json;
		
	}
	
	
	
	
	
	
	public String cnb_form(HashMap<String, String> list){			
	
			String language = "ru";
			if(list.get("language") != null)
				language = list.get("language");			
			
			String signature = cnb_signature(list);
			
			list.put("public_key", pub_key);
			list.put("signature", signature);
			
			String form = "";		
			form += "<form method=\"post\" action=\"https://www.liqpay.com/api/pay\" accept-charset=\"utf-8\">\n";
			
			for (Map.Entry<String, String> entry: list.entrySet())
				form += "<input type=\"hidden\" name=\""+entry.getKey()+"\" value=\""+entry.getValue()+"\" />\n";			
			
			form += "<input type=\"image\" src=\"//static.liqpay.com/buttons/p1"+language+".radius.png\" name=\"btn_text\" />\n";
			form += "</form>\n";
	
			return form;
			
	}
	
	
	
	
	public String cnb_signature(HashMap<String, String> list){
		
		String amount = list.get("amount");
		String currency = list.get("currency");
		String order_id = list.get("order_id");
		String type = list.get("type");
		String description = list.get("description");
		String result_url = list.get("result_url");
		String server_url = list.get("server_url");
		String first_name = list.get("sender_first_name");
		String last_name = list.get("sender_last_name");
		String middle_name = list.get("sender_middle_name");
		String country_code = list.get("sender_country");
		String city_name = list.get("sender_city");
		String address = list.get("sender_address");
		String postal_code = list.get("sender_postal_code");
		
				
		if(amount == null)
			throw new NullPointerException("amount can't be null");		
		if(currency == null)
			throw new NullPointerException("currency can't be null");		
		if(description == null)
			throw new NullPointerException("description can't be null");
		

		String sign_str = priv_key + amount + currency + pub_key;
		
		if(order_id != null)sign_str += order_id;
		if(type != null)sign_str += type;
		if(description != null)sign_str += description;
		if(result_url != null)sign_str += result_url;
		if(server_url != null)sign_str += server_url;
		if(first_name != null)sign_str += first_name;
		if(last_name != null)sign_str += last_name;
		if(middle_name != null)sign_str += middle_name;
		if(country_code != null)sign_str += country_code;
		if(city_name != null)sign_str += city_name;
		if(address != null)sign_str += address;
		if(postal_code != null)sign_str += postal_code;				
		
		return str_to_sign(sign_str);
		
	}
	
	
	
	public String str_to_sign(String str){				
		
		String signature = LiqPayUtil.base64_encode( LiqPayUtil.sha1( str ) );

		return signature;
		
	}
	
	
	
	public static void main(String [] args)
	{
		
	}
	

}
