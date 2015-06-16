package com.expanset.samples.complex.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.jvnet.hk2.annotations.Contract;
import org.jvnet.hk2.annotations.Service;

import com.expanset.hk2.config.ConfigurationReloadListener;
import com.expanset.hk2.config.ConfiguredString;
import com.expanset.hk2.logging.CorrelationIdType;
import com.expanset.hk2.logging.LogLevel;
import com.expanset.hk2.logging.Loggable;

@Contract
@Service
public class StockQuotesService implements ConfigurationReloadListener {
	
	private AtomicReference<WebTarget> stockQuotesTarget = new AtomicReference<WebTarget>();
	
	@ConfiguredString(value="stockQuotesUrl",required=true)
	private Supplier<String> stockQuotesUrl;

	@ConfiguredString(value="dateField",required=true)
	private Supplier<String> dateField;

	@ConfiguredString(value="valueField",required=true)
	private Supplier<String> valueField;
	
	@PostConstruct
	public void initialize() {
		stockQuotesTarget.set(ClientBuilder.newClient().target(stockQuotesUrl.get()));
	}
	
	@Loggable(idType=CorrelationIdType.SIMPLE, value=LogLevel.INFO, measure=true)
	public List<StockQuote> queryGoogle(String symbol) 
			throws IOException {
		
		final InputStream stream = stockQuotesTarget.get()
				.resolveTemplate("symbol", symbol)
				.request(MediaType.TEXT_PLAIN_TYPE)
				.get(InputStream.class);
		try(final InputStreamReader reader = new InputStreamReader(new BOMInputStream(stream));) {
			final Iterable<CSVRecord> records = CSVFormat.EXCEL
					.withHeader()
					.parse(reader);
			
			final List<StockQuote> stockQuotes = StreamSupport.stream(records.spliterator(), false)
			 	.map(item -> new StockQuote(
			 			item.get(dateField.get()), 
			 			Double.parseDouble(item.get(valueField.get()))))
			 	.collect(Collectors.<StockQuote>toList());
			
			return stockQuotes;
		}
	}

	@Override
	public void configurationReloaded() {
		initialize();
	}
}
