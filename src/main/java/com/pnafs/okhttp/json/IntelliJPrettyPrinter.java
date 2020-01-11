package com.pnafs.okhttp.json;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class IntelliJPrettyPrinter extends DefaultPrettyPrinter {
    public IntelliJPrettyPrinter() {
        super();
        _objectFieldValueSeparatorWithSpaces = ":";
        _arrayIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
    }
}
