package org.example.store;

public enum Queries {
    SELECT_CURR("SELECT * FROM currencies"),
    SELECT_CURR_BY_CODE("SELECT * FROM main.currencies WHERE code = ?"),
    INSERT_INTO_CURR("INSERT INTO main.currencies(code, full_name, sign) VALUES (?, ?, ?)"),
    INSERT_INTO_RATES("""
            INSERT INTO exchange_rates (base_current_id, target_current_id, rate)
                        SELECT
                            base.id AS base_current_id,
                            target.id AS target_current_id,
                            ? AS rate
                        FROM currencies base, currencies target
                        WHERE base.code = ? AND target.code = ?
            """),
    SELECT_FROM_RATES_LAST("""
            SELECT
                er.id,
                er.base_current_id,
                er.target_current_id,
                er.rate,
                base_curr.code AS base_code,
                target_curr.code AS target_code,
                base_curr.full_name AS base_full_name,
                target_curr.full_name AS target_full_name,
                base_curr.sign AS base_sign,
                target_curr.sign AS target_sign
            FROM exchange_rates AS er
            JOIN currencies base_curr ON er.base_current_id = base_curr.id
            JOIN currencies target_curr ON er.target_current_id = target_curr.id
            WHERE er.id = last_insert_rowid()
            """),
    UPDATE_RATES("""
            UPDATE exchange_rates
                SET rate = ?
                WHERE (base_current_id, target_current_id) = (
                    SELECT base.id, target.id
                    FROM currencies base, currencies target
                    WHERE base.code = ? AND target.code = ?
                )
            """),
    SELECT_FROM_RATES("""
            SELECT
                    er.id,
                    er.base_current_id,
                    er.target_current_id,
                    er.rate,
                    base_curr.code AS base_code,
                    target_curr.code AS target_code,
                    base_curr.full_name AS base_full_name,
                    target_curr.full_name AS target_full_name,
                    base_curr.sign AS base_sign,
                    target_curr.sign AS target_sign
                FROM exchange_rates AS er
                JOIN currencies base_curr ON er.base_current_id = base_curr.id
                JOIN currencies target_curr ON er.target_current_id = target_curr.id
                WHERE base_curr.code = ? AND target_curr.code = ?
            """),
    SELECT_RATES_BY_CODES("""
            SELECT
                    er.id,
                    er.base_current_id,
                    er.target_current_id,
                    er.rate,
                    base_curr.code AS base_code,
                    target_curr.code AS target_code,
                    base_curr.full_name AS base_full_name,
                    target_curr.full_name AS target_full_name,
                    base_curr.sign AS base_sign,
                    target_curr.sign AS target_sign
                FROM exchange_rates AS er
                JOIN currencies base_curr ON er.base_current_id = base_curr.id
                JOIN currencies target_curr ON er.target_current_id = target_curr.id
                WHERE base_curr.code = ? AND target_curr.code = ?
            """),
    SELECT_ALL_RATES("""
            SELECT
                  ex.id,
                  ex.base_current_id,
                  ex.target_current_id,
                  ex.rate,
                  c.code AS base_code,
                  c2.code AS target_code,
                  c.full_name AS base_full_name,
                  c2.full_name AS target_full_name,
                  c.sign AS base_sign,
                  c2.sign AS target_sign
              FROM exchange_rates AS ex
              LEFT JOIN currencies c on c.id = ex.base_current_id
              LEFT JOIN currencies c2 on c2.id = ex.target_current_id;
            """);

    private String stmnt;

    Queries(String statement) {
        this.stmnt = statement;
    }

    public String getStmnt() {
        return stmnt;
    }
}
