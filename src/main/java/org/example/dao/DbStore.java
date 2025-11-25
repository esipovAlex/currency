package org.example.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.example.dao.commands.*;
import org.example.exception.CurrencyNotFoundException;
import org.example.model.entity.Currency;
import org.example.model.request.ExcRateCode;
import org.example.model.request.ExchCreateRequest;
import org.example.model.response.ExRateRespDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static org.example.exceptionhandler.MessageErr.CURRENCY_NOT_FOUND;
public class DbStore implements Store {
    private static final Logger LOG = LoggerFactory.getLogger(DbStore.class.getName());

    private final BasicDataSource pool = new BasicDataSource();
    private final CommandExecutor executor;

    private DbStore() {
        this.executor = new CommandExecutor(pool);
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(DbStore.class.getClassLoader()
                                .getResourceAsStream("db.properties"))
                )
        )) {
            cfg.load(io);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        String webInfPath = Objects.requireNonNull(getClass().getClassLoader().getResource("")).getPath();
        String url = "jdbc:sqlite:" + webInfPath + "mydb.db";
        pool.setUrl(url);
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INSTANCE = new DbStore();
    }

    public static Store instOf() {
        return Lazy.INSTANCE;
    }

    @Override
    public Currency save(Currency currency) {
        return executor.executeTransaction(new SaveCurrencyCommand(currency));
    }

    @Override
    public Currency findByCode(String code) {
        Currency result = executor.execute(new FindCurrencyByCodeCommand(code));
        if (result == null) {
            throw new CurrencyNotFoundException(CURRENCY_NOT_FOUND.getMessage());
        }
        return result;
    }

    @Override
    public List<Currency> findAllCurrencies() {
        return executor.execute(new FindAllCurrenciesCommand());
    }

    @Override
    public ExRateRespDto save(ExchCreateRequest exchangeRate) {
        return executor.executeTransaction(new SaveExchangeRateCommand(exchangeRate));
    }

    @Override
    public ExRateRespDto update(ExchCreateRequest exchangeRate) {
        return executor.executeTransaction(new UpdateExchangeRateCommand(exchangeRate));
    }

    @Override
    public ExRateRespDto findByCodes(ExcRateCode exchangeRate) {
        return executor.executeTransaction(new FindByCodesCommand(exchangeRate));
    }

    @Override
    public List<ExRateRespDto> findAllExRate() {
        return executor.execute(new FindAllExchangeRatesCommand());
    }
}