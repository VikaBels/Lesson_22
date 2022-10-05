package com.example.lesson20;

import android.util.Log;

import com.example.lesson20.javaAttribute.TesterAttribute;
import com.example.lesson20.javaAttribute.TesterMethod;

public class Tester {
    public Tester(String param) {
        this.param = param;
    }

    @TesterAttribute(info = "Some attribute")
    private final String param;

    protected int protectedParam = 42;

    @TesterMethod(description = "Some public method")
    public void doPublic() {
        Log.e("TAG", "protected: " + param);
    }

    protected void doProtected() {
        Log.e("TAG", "protected: " + param + " (" + protectedParam + ")");
    }

    @TesterMethod(description = "Some private method", isInner = true)
    private void doPrivate() {
        Log.e("TAG", "private: " + param);
    }
}
