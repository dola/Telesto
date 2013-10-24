package ch.ethz.syslab.telesto.server.db;

import java.sql.Types;

public enum StoredProcedure {

    IDENTIFY("identify", new int[] { Types.INTEGER }, ReturnType.CLIENT_TABLE),
    REQUEST_ID("request_id", new int[] { Types.VARCHAR, Types.SMALLINT }, ReturnType.INTEGER),
    CREATE_QUEUE("create_queue", new int[] { Types.VARCHAR }, ReturnType.QUEUE_TABLE);

    private final String methodName;
    private final int[] argumentTypes;
    private final ReturnType returnType;

    StoredProcedure(String methodName, int[] argumentTypes, ReturnType returnType) {
        this.methodName = methodName;
        this.argumentTypes = argumentTypes;
        this.returnType = returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getArgumentCount() {
        return argumentTypes.length;
    }

    public int[] getArgumentTypes() {
        return argumentTypes;
    }

    public ReturnType getReturnType() {
        return returnType;
    }

    public boolean hasReturnValue() {
        return returnType != null && !returnType.equals(ReturnType.NONE);
    }

    public boolean hasSingleReturnValue() {
        return hasReturnValue() && returnType.isSingleResult();
    }
}
