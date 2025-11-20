package org.example.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.example.exception.CodeCurrenciesException;
import org.example.exception.CurrencyAlreadyExistsException;
import org.example.exception.CurrencyNotFoundException;
import org.example.exception.DataBaseErrorException;
import org.example.exceptionhandler.MessageErr;
import org.example.model.entity.Currency;
import org.example.model.request.ExcRateCode;
import org.example.model.request.ExchCreateRequest;
import org.example.model.response.CurrencyDto;
import org.example.model.response.ExRateRespDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.example.exceptionhandler.MessageErr.*;

public class DbStore implements Store {
    private static final Logger LOG = LoggerFactory.getLogger(DbStore.class.getName());
    private final BasicDataSource pool = new BasicDataSource();

    private DbStore() {
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
        Currency result = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(Queries.INSERT_INTO_CURR.getStmnt(),
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, currency.code());
            ps.setString(2, currency.fullName());
            ps.setString(3, currency.sign());
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    result = fillCurrency(rs, currency);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            String message = e.getMessage();
            if (message.contains("[SQLITE_CONSTRAINT_UNIQUE]")) {
                throw new CurrencyAlreadyExistsException(CURRENCY_ALREADY_EXIST.getMessage());
            }
            throw new DataBaseErrorException(DATABASE_ERROR.getMessage());
        }
        return result;
    }

    @Override
    public Currency findByCode(String code) {
        Currency result = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(Queries.SELECT_CURR_BY_CODE.getStmnt())) {
            ps.setString(1, code);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    result = createCurrency(it);
                }
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            String message = e.getMessage();
            if (message.contains("[SQLITE_ERROR]")) {
                throw new DataBaseErrorException(DATABASE_ERROR.getMessage());
            }
        }
        if (result == null) {
            throw new CurrencyNotFoundException(CURRENCY_NOT_FOUND.getMessage());
        }
        return result;
    }

    @Override
    public List<Currency> findAllCurrencies() {
        List<Currency> result = new ArrayList<>();
        try (Connection cn = pool.getConnection();
            PreparedStatement ps = cn.prepareStatement(Queries.SELECT_CURR.getStmnt())) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    result.add(createCurrency(resultSet));
                }
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            throw new DataBaseErrorException(DATABASE_ERROR.getMessage());
        }
        return result;
    }

    @Override
    public ExRateRespDto save(ExchCreateRequest exchangeRate) {
        ExRateRespDto result;
        Connection cn = null;
        try {
            cn = pool.getConnection();
            cn.setAutoCommit(false);
            try (PreparedStatement psInsert = cn.prepareStatement(Queries.INSERT_INTO_RATES.getStmnt())) {
                psInsert.setBigDecimal(1, exchangeRate.rate());
                psInsert.setString(2, exchangeRate.base());
                psInsert.setString(3, exchangeRate.target());
                int affectedRows = psInsert.executeUpdate();
                if (affectedRows == 0) {
                    throw new CodeCurrenciesException(MessageErr.CURRENCY_PAIR_NOT_EXIST.getMessage());
                }
            }
            try (PreparedStatement psSelect = cn.prepareStatement(Queries.SELECT_FROM_RATES_LAST.getStmnt())) {
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataBaseErrorException(DATABASE_ERROR.getMessage());
                    }
                    cn.commit();
                    result = createExRateResp(rs);
                }
            }
        } catch (CodeCurrenciesException e) {
            if (Objects.nonNull(cn)) {
                try {
                    cn.rollback();
                } catch (SQLException rollbackEx) {
                    LOG.error("Failed to rollback transaction", rollbackEx);
                    throw new DataBaseErrorException(DATABASE_ERROR.getMessage());
                }
            }
            LOG.error(e.getMessage(), e);
            throw new CodeCurrenciesException(e.getMessage());
        } catch (Exception e) {
            if (Objects.nonNull(cn)) {
                try {
                    cn.rollback();
                } catch (SQLException rollbackEx) {
                    LOG.error("Failed to rollback transaction", rollbackEx);
                }
            }
            LOG.error(e.getMessage(), e);
            String message = e.getMessage();
            if (message != null && message.contains("[SQLITE_CONSTRAINT_UNIQUE]")) {
                throw new CurrencyAlreadyExistsException(CURRENCY_PAIR_ALREADY_EXIST.getMessage());
            }
            throw new DataBaseErrorException(DATABASE_ERROR.getMessage());
        } finally {
            if (Objects.nonNull(cn)) {
                try {
                    cn.setAutoCommit(true);
                    cn.close();
                } catch (SQLException closeEx) {
                    LOG.error("Failed to close connection", closeEx);
                }
            }
        }
        return result;
    }

    @Override
    public ExRateRespDto update(ExchCreateRequest exchangeRate) {
        ExRateRespDto result;
        Connection cn = null;
        try {
            cn = pool.getConnection();
            cn.setAutoCommit(false);
            try (PreparedStatement psUpdate = cn.prepareStatement(Queries.UPDATE_RATES.getStmnt())) {
                psUpdate.setBigDecimal(1, exchangeRate.rate());
                psUpdate.setString(2, exchangeRate.base());
                psUpdate.setString(3, exchangeRate.target());
                int affectedRows = psUpdate.executeUpdate();
                if (affectedRows == 0) {
                    throw new CurrencyNotFoundException(CURRENCY_PAIR_NOT_EXIST.getMessage());
                }
            }
            try (PreparedStatement psSelect = cn.prepareStatement(Queries.SELECT_FROM_RATES.getStmnt())) {
                psSelect.setString(1, exchangeRate.base());
                psSelect.setString(2, exchangeRate.target());
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (!rs.next()) {
                        throw new CurrencyNotFoundException(CURRENCY_PAIR_NOT_EXIST.getMessage());
                    }
                    cn.commit();
                    result = createExRateResp(rs);
                }
            }

        } catch (CurrencyNotFoundException e) {
            if (Objects.nonNull(cn)) {
                try {
                    cn.rollback();
                } catch (SQLException rollbackEx) {
                    LOG.error("Failed to rollback transaction", rollbackEx);
                    throw new DataBaseErrorException(DATABASE_ERROR.getMessage());
                }
            }
            LOG.error(e.getMessage(), e);
            throw new CurrencyNotFoundException(e.getMessage());
        } catch (Exception e) {
            if (Objects.nonNull(cn)) {
                try {
                    cn.rollback();
                } catch (SQLException rollbackEx) {
                    LOG.error("Failed to rollback transaction", rollbackEx);
                    throw new DataBaseErrorException(DATABASE_ERROR.getMessage());
                }
            }
            LOG.error(e.getMessage(), e);
            throw new DataBaseErrorException(DATABASE_ERROR.getMessage());
        } finally {
            if (Objects.nonNull(cn)) {
                try {
                    cn.setAutoCommit(true);
                    cn.close();
                } catch (SQLException closeEx) {
                    LOG.error("Failed to close connection", closeEx);
                }
            }
        }
        return result;
    }

    @Override
    public ExRateRespDto findByCodes(ExcRateCode exchangeRate) {
        ExRateRespDto result;
        Connection cn = null;
        try {
            cn = pool.getConnection();
            cn.setAutoCommit(false);
            try (PreparedStatement psSelect = cn.prepareStatement(Queries.SELECT_RATES_BY_CODES.getStmnt())) {
                psSelect.setString(1, exchangeRate.base());
                psSelect.setString(2, exchangeRate.target());
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (!rs.next()) {
                        throw new CurrencyNotFoundException(CURRENCY_PAIR_NOT_EXIST.getMessage());
                    }
                    cn.commit();
                    result = createExRateResp(rs);
                }
            }
        } catch (Exception e) {
            if (Objects.nonNull(cn)) {
                try {
                    cn.rollback();
                } catch (SQLException rollbackEx) {
                    LOG.error("Failed to rollback transaction", rollbackEx);
                }
            }
            LOG.error(e.getMessage(), e);
            String message = e.getMessage();
            if (Objects.nonNull(message) && message.contains("[SQLITE_CONSTRAINT_UNIQUE]")) {
                throw new CurrencyAlreadyExistsException(CURRENCY_ALREADY_EXIST.getMessage());
            }
            throw new DataBaseErrorException(DATABASE_ERROR.getMessage());
        } finally {
            if (Objects.nonNull(cn)) {
                try {
                    cn.setAutoCommit(true);
                    cn.close();
                } catch (SQLException closeEx) {
                    LOG.error("Failed to close connection", closeEx);
                }
            }
        }
        return result;
    }

    @Override
    public List<ExRateRespDto> findAllExRate() {
        List<ExRateRespDto> result = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(Queries.SELECT_ALL_RATES.getStmnt())) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    result.add(createExRateResp(resultSet));
                }
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            throw new DataBaseErrorException(DATABASE_ERROR.getMessage());
        }
        return result;
    }

    private ExRateRespDto createExRateResp(ResultSet rs) throws SQLException {
        return new ExRateRespDto(
                rs.getInt("id"),
                new CurrencyDto(
                        rs.getInt("base_current_id"),
                        rs.getString("base_code"),
                        rs.getString("base_full_name"),
                        rs.getString("base_sign")
                ),
                new CurrencyDto(
                        rs.getInt("target_current_id"),
                        rs.getString("target_code"),
                        rs.getString("target_full_name"),
                        rs.getString("target_sign")
                ),
                rs.getBigDecimal("rate")
        );
    }

    private Currency createCurrency(ResultSet rs) throws SQLException {
        return new Currency(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("full_name"),
                rs.getString("sign")
                );
    }

    private Currency fillCurrency(ResultSet rs, Currency old) throws SQLException {
        return new Currency(rs.getInt(1), old.code(), old.fullName(), old.sign());
    }
}
