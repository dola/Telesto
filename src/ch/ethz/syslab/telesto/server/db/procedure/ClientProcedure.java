package ch.ethz.syslab.telesto.server.db.procedure;

import java.sql.Types;

import ch.ethz.syslab.telesto.server.db.ReturnType;

public enum ClientProcedure implements StoredProcedure {

    IDENTIFY("identify", new int[] { Types.INTEGER }, ReturnType.CLIENT_TABLE),
    REQUEST_ID("request_id", new int[] { Types.VARCHAR, Types.SMALLINT }, ReturnType.INTEGER),
    DELETE_CLIENT("delete_client", new int[] { Types.INTEGER }, ReturnType.INTEGER);

    private final String methodName;
    private final int[] argumentTypes;
    private final ReturnType returnType;

    ClientProcedure(String methodName, int[] argumentTypes, ReturnType returnType) {
        this.methodName = methodName;
        this.argumentTypes = argumentTypes;
        this.returnType = returnType;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public int getArgumentCount() {
        return argumentTypes.length;
    }

    @Override
    public int[] getArgumentTypes() {
        return argumentTypes;
    }

    @Override
    public ReturnType getReturnType() {
        return returnType;
    }

    @Override
    public boolean hasReturnValue() {
        return returnType != null && !returnType.equals(ReturnType.NONE);
    }

    @Override
    public boolean hasSingleReturnValue() {
        return hasReturnValue() && returnType.isSingleResult();
    }
}
