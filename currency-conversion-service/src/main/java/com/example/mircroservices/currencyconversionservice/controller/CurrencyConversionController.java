package com.example.mircroservices.currencyconversionservice.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.mircroservices.currencyconversionservice.bean.CurrencyConverionBean;
import com.example.mircroservices.currencyconversionservice.proxy.CurrencyExchangeServiceProxy;

@RestController
public class CurrencyConversionController {

	private Logger logger =  LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CurrencyExchangeServiceProxy proxy;
	
	//without feign
	@GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConverionBean convertCurrency(@PathVariable String from,
												 @PathVariable String to,
												 @PathVariable BigDecimal quantity) {
		
		Map<String, String> uriVariables =new HashMap<String, String>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		
		ResponseEntity<CurrencyConverionBean> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConverionBean.class, uriVariables );
		CurrencyConverionBean response = responseEntity.getBody();
		return new CurrencyConverionBean(response.getId(), from, to, response.getConversionMultiple(), quantity, response.getConversionMultiple().multiply(quantity), response.getPort());
	}
	
	// using proxy feign
	@GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConverionBean convertCurrencyFeign(@PathVariable String from,
												 @PathVariable String to,
												 @PathVariable BigDecimal quantity) {
		
		CurrencyConverionBean response = proxy.retrieveExchangeValue(from, to);
		logger.info("{}",response);
		return new CurrencyConverionBean(response.getId(), from, to, response.getConversionMultiple(), quantity, response.getConversionMultiple().multiply(quantity), response.getPort());
	}
}
