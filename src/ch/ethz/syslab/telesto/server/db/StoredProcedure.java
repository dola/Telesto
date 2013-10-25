package ch.ethz.syslab.telesto.server.db;

import java.sql.Types;

public enum StoredProcedure {

    IDENTIFY("identify", new int[] { Types.INTEGER }, ReturnType.CLIENT_TABLE),
    REQUEST_ID("request_id", new int[] { Types.VARCHAR, Types.SMALLINT }, ReturnType.INTEGER),

    CREATE_QUEUE("create_queue", new int[] { Types.VARCHAR }, ReturnType.QUEUE_TABLE),
    DELETE_QUEUE("delete_queue", new int[] { Types.INTEGER }, ReturnType.QUEUE_TABLE),
    GET_QUEUE_NAME("get_queue_name", new int[] { Types.INTEGER }, ReturnType.QUEUE_TABLE),
    GET_QUEUE_ID("get_queue_id", new int[] { Types.VARCHAR }, ReturnType.QUEUE_TABLE),
    LIST_QUEUES("list_queues", new int[0], ReturnType.QUEUE_TABLE),

    GET_ACTIVE_QUEUES("get_active_queues", new int[] { Types.INTEGER }, ReturnType.QUEUE_TABLE),

    GET_MESSAGES_FROM_QUEUE("get_messages_from_queue", new int[] { Types.INTEGER }, ReturnType.MESSAGE_TABLE),

    PUT_MESSAGE("put_message", new int[] { Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.SMALLINT, Types.VARCHAR },
            ReturnType.NONE),
    PUT_MESSAGES("put_messages", new int[] { Types.ARRAY, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.SMALLINT, Types.VARCHAR },
            ReturnType.NONE),

    READ_MESSAGE_BY_PRIORITY("read_message_by_priority", new int[] { Types.INTEGER, Types.INTEGER, Types.INTEGER }, ReturnType.MESSAGE_TABLE),
    READ_MESSAGE_BY_TIMESTAMP("read_message_by_timestamp", new int[] { Types.INTEGER, Types.INTEGER, Types.INTEGER }, ReturnType.MESSAGE_TABLE);

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
