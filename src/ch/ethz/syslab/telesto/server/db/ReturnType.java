package ch.ethz.syslab.telesto.server.db;

import java.sql.Types;

public enum ReturnType {
    NONE(Types.NULL, true),
    INTEGER(Types.INTEGER, true),
    INTEGER_TABLE(Types.OTHER, false),
    CLIENT_TABLE(Types.OTHER, false),
    QUEUE_TABLE(Types.OTHER, false),
    MESSAGE_TABLE(Types.OTHER, false);

    private final int sqlType;
    private final boolean singleResult;

    ReturnType(int sqlType, boolean singleResult) {
        this.sqlType = sqlType;
        this.singleResult = singleResult;
    }

    public int getSqlType() {
        return sqlType;
    }

    public boolean isSingleResult() {
        return singleResult;
    }
}
