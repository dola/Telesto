package ch.ethz.syslab.telesto.server.db.procedure;

import java.sql.Types;

import ch.ethz.syslab.telesto.server.db.ReturnType;

public enum MessageProcedure implements StoredProcedure {

    GET_MESSAGES_FROM_QUEUE("get_messages_from_queue", new int[] { Types.INTEGER }, ReturnType.MESSAGE_TABLE),

    PUT_MESSAGE("put_message", new int[] { Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.SMALLINT, Types.VARCHAR },
            ReturnType.INTEGER),
    PUT_MESSAGES("put_messages", new int[] { Types.ARRAY, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.SMALLINT, Types.VARCHAR },
            ReturnType.INTEGER_TABLE),

    READ_MESSAGE_BY_PRIORITY("read_message_by_priority", new int[] { Types.INTEGER, Types.INTEGER, Types.INTEGER }, ReturnType.MESSAGE_TABLE),
    READ_MESSAGE_BY_TIMESTAMP("read_message_by_timestamp", new int[] { Types.INTEGER, Types.INTEGER, Types.INTEGER }, ReturnType.MESSAGE_TABLE);

    private final String methodName;
    private final int[] argumentTypes;
    private final ReturnType returnType;

    MessageProcedure(String methodName, int[] argumentTypes, ReturnType returnType) {
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
