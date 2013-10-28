package ch.ethz.syslab.telesto.server.db.procedure;

import java.sql.Types;

import ch.ethz.syslab.telesto.server.db.ReturnType;

public enum QueueProcedure implements StoredProcedure {

    CREATE_QUEUE("create_queue", new int[] { Types.VARCHAR }, ReturnType.QUEUE_TABLE),
    DELETE_QUEUE("delete_queue", new int[] { Types.INTEGER }, ReturnType.QUEUE_TABLE),
    GET_QUEUE_NAME("get_queue_name", new int[] { Types.INTEGER }, ReturnType.QUEUE_TABLE),
    GET_QUEUE_ID("get_queue_id", new int[] { Types.VARCHAR }, ReturnType.QUEUE_TABLE),
    LIST_QUEUES("list_queues", new int[0], ReturnType.QUEUE_TABLE);

    private final String methodName;
    private final int[] argumentTypes;
    private final ReturnType returnType;

    QueueProcedure(String methodName, int[] argumentTypes, ReturnType returnType) {
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
