package com;

import com.dao.BankTransaction;
import com.dao.BankTransactionItemAnalyticsProcessor;
import com.dao.BankTransactionItemProcessor;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.Delimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
  @Autowired
  private JobBuilderFactory jobBuilderFactory ;
  @Autowired
  private StepBuilderFactory stepBuilderFactory ;
  @Autowired
  private ItemReader<BankTransaction> bankTransactionItemReader ;
  @Autowired
  private ItemWriter<BankTransaction> bankTransactionItemWriter ;
  //@Autowired private ItemProcessor<BankTransaction , BankTransaction> bankTransactionItemProcessor ;


  @Bean
  public Job bankJob() {

      Step step1 = stepBuilderFactory.get("step-load-data")
              .<BankTransaction , BankTransaction>chunk(100)
              .reader(bankTransactionItemReader)
              .processor(compositeItemProcessor())
              .writer(bankTransactionItemWriter)
              .build() ;

      return  jobBuilderFactory.get("bank_data-loader-job")
              .start(step1).build() ;

  }

  @Bean
  public  ItemProcessor<BankTransaction,BankTransaction> compositeItemProcessor() {

      List<ItemProcessor<BankTransaction , BankTransaction>> itemProcessors = new ArrayList<>() ;
      itemProcessors.add(itemProcessor1());
      itemProcessors.add(itemProcessor2());

      CompositeItemProcessor<BankTransaction , BankTransaction> compositeItemProcessor = new CompositeItemProcessor<>() ;
      compositeItemProcessor.setDelegates(itemProcessors);
      return  compositeItemProcessor ;
    }

    @Bean
  BankTransactionItemProcessor itemProcessor1(){
      return  new BankTransactionItemProcessor() ;
  }

  @Bean
    BankTransactionItemAnalyticsProcessor itemProcessor2(){
        return  new BankTransactionItemAnalyticsProcessor() ;
    }


    @Bean
  public FlatFileItemReader<BankTransaction> flatFileItemReader(@Value("${inputFile}") Resource inputFile){
      FlatFileItemReader<BankTransaction> flatItemReader = new FlatFileItemReader<>() ;
      flatItemReader.setName("FFIR1");
      flatItemReader.setLinesToSkip(1);
      flatItemReader.setResource(inputFile );
      flatItemReader.setLineMapper(lineMappe());
      return  flatItemReader ;

  }

  @Bean
  public LineMapper<BankTransaction> lineMappe() {

      DefaultLineMapper<BankTransaction> lineMapper = new DefaultLineMapper<>() ;
      DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer() ;
      lineTokenizer.setDelimiter(",");
      lineTokenizer.setStrict(false);
      lineTokenizer.setNames("id","accountID","strTransactionDate","transactionType","amount");
      lineMapper.setLineTokenizer(lineTokenizer);
      BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper() ;
      fieldSetMapper.setTargetType(BankTransaction.class);
      lineMapper.setFieldSetMapper(fieldSetMapper);

      return  lineMapper ;

    }



}
